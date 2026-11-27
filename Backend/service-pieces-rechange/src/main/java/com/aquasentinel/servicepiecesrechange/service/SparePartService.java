package com.aquasentinel.servicepiecesrechange.service;

import com.aquasentinel.servicepiecesrechange.exception.ConflictException;
import com.aquasentinel.servicepiecesrechange.exception.NotFoundException;
import com.aquasentinel.servicepiecesrechange.model.*;
import com.aquasentinel.servicepiecesrechange.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SparePartService {

    private final SparePartRepository partRepo;
    private final PartCategoryRepository categoryRepo;
    private final SupplierRepository supplierRepo;
    private final ReservationRepository reservationRepo;
    private final StockMovementRepository movementRepo;

    public record SparePartInput(
            String reference,
            String name,
            String description,
            String unit,
            Integer quantityInStock,
            Integer minThreshold,
            Integer maxCapacity,
            BigDecimal unitPrice,
            String storageLocation,
            Long categoryId,
            Long supplierId
    ) {}

    public record RestockSuggestion(
            Long partId,
            String reference,
            String name,
            PartStatus status,
            int quantityInStock,
            int reservedQuantity,
            int availableQuantity,
            int minThreshold,
            Integer maxCapacity,
            int suggestedOrderQuantity,
            String supplierName,
            Integer leadTimeDays,
            BigDecimal estimatedOrderCost
    ) {}

    public record StockValuation(
            int totalPartsCount,
            int totalUnitsInStock,
            int totalReservedUnits,
            BigDecimal totalStockValue,
            Map<String, BigDecimal> valueByCategory
    ) {}

    public record MonthlyConsumptionStat(
            String month,
            String partReference,
            int totalConsumedQuantity
    ) {}

    // ---------- CRUD SparePart ----------
    public List<SparePart> findAll(Long categoryId, Long supplierId, PartStatus status, String search) {
        String cleanSearch = (search != null && !search.isBlank()) ? search.trim() : null;
        return partRepo.search(categoryId, supplierId, status, cleanSearch);
    }

    public SparePart findById(Long id) {
        return partRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Pièce introuvable avec id : " + id));
    }

    public SparePart findByReference(String reference) {
        return partRepo.findByReferenceIgnoreCase(reference)
                .orElseThrow(() -> new NotFoundException("Pièce introuvable avec référence : " + reference));
    }

    @Transactional
    public SparePart create(SparePartInput input) {
        String normalizedRef = input.reference().trim().toUpperCase();
        if (partRepo.existsByReferenceIgnoreCase(normalizedRef)) {
            throw new ConflictException("Une pièce avec cette référence existe déjà : " + normalizedRef);
        }
        validateCapacityBounds(input.quantityInStock(), input.minThreshold(), input.maxCapacity());

        PartCategory category = resolveCategory(input.categoryId());
        Supplier supplier = resolveSupplier(input.supplierId());

        SparePart part = SparePart.builder()
                .reference(normalizedRef)
                .name(input.name())
                .description(input.description())
                .unit(input.unit() != null && !input.unit().isBlank() ? input.unit() : "pièce")
                .quantityInStock(input.quantityInStock())
                .reservedQuantity(0)
                .minThreshold(input.minThreshold())
                .maxCapacity(input.maxCapacity())
                .unitPrice(input.unitPrice())
                .storageLocation(input.storageLocation())
                .category(category)
                .supplier(supplier)
                .build();
        part.refreshStatus();
        SparePart saved = partRepo.save(part);

        if (saved.getQuantityInStock() > 0) {
            recordMovement(saved, null, MovementType.ENTRY, saved.getQuantityInStock(), "INIT-" + saved.getReference());
        }
        return saved;
    }

    @Transactional
    public SparePart update(Long id, SparePartInput input) {
        SparePart existing = findById(id);
        String newRef = input.reference().trim().toUpperCase();
        if (!existing.getReference().equalsIgnoreCase(newRef) && partRepo.existsByReferenceIgnoreCase(newRef)) {
            throw new ConflictException("Une autre pièce utilise déjà la référence : " + newRef);
        }
        validateCapacityBounds(input.quantityInStock(), input.minThreshold(), input.maxCapacity());
        if (input.quantityInStock() < existing.getReservedQuantity()) {
            throw new ConflictException("Le stock ne peut pas être inférieur à la quantité réservée (" + existing.getReservedQuantity() + ")");
        }

        existing.setReference(newRef);
        existing.setName(input.name());
        existing.setDescription(input.description());
        existing.setUnit(input.unit() != null && !input.unit().isBlank() ? input.unit() : existing.getUnit());
        existing.setQuantityInStock(input.quantityInStock());
        existing.setMinThreshold(input.minThreshold());
        existing.setMaxCapacity(input.maxCapacity());
        existing.setUnitPrice(input.unitPrice());
        existing.setStorageLocation(input.storageLocation());
        existing.setCategory(resolveCategory(input.categoryId()));
        existing.setSupplier(resolveSupplier(input.supplierId()));
        existing.refreshStatus();
        return partRepo.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        SparePart existing = findById(id);
        if (existing.getReservedQuantity() != null && existing.getReservedQuantity() > 0) {
            throw new ConflictException("Impossible de supprimer une pièce ayant du stock réservé (" + existing.getReservedQuantity() + ")");
        }
        partRepo.delete(existing);
    }

    // ---------- Ajustement manuel ----------
    @Transactional
    public SparePart adjustStock(Long id, int delta, String movementReference) {
        if (delta == 0) {
            throw new IllegalArgumentException("L'ajustement de stock (delta) doit être différent de 0");
        }
        SparePart part = findById(id);
        int newQty = part.getQuantityInStock() + delta;
        if (newQty < 0) {
            throw new IllegalArgumentException("Stock insuffisant pour effectuer ce retrait");
        }
        if (newQty < part.getReservedQuantity()) {
            throw new ConflictException("Le stock total ne peut pas descendre sous la quantité réservée (" + part.getReservedQuantity() + ")");
        }
        if (part.getMaxCapacity() != null && newQty > part.getMaxCapacity()) {
            throw new IllegalArgumentException("Le stock dépasserait la capacité maximale (" + part.getMaxCapacity() + ")");
        }
        part.setQuantityInStock(newQty);
        if (delta > 0) {
            part.setLastRestockedAt(LocalDateTime.now());
        }
        part.refreshStatus();
        SparePart saved = partRepo.save(part);

        MovementType type = delta > 0 ? MovementType.ENTRY : MovementType.EXIT;
        String ref = (movementReference != null && !movementReference.isBlank()) ? movementReference : "MANUAL-" + saved.getReference();
        recordMovement(saved, null, type, Math.abs(delta), ref);
        return saved;
    }

    // ---------- Réservations (Maintenance <-> Spare Parts) ----------
    @Transactional
    public Reservation reserve(String partReference, int quantity, Long maintenanceRequestId) {
        if (maintenanceRequestId == null) {
            throw new IllegalArgumentException("maintenanceRequestId est obligatoire");
        }
        SparePart part = findByReference(partReference);

        Reservation existing = reservationRepo.findByMaintenanceRequestIdAndSparePartId(maintenanceRequestId, part.getId())
                .orElse(null);
        if (existing != null && existing.getStatus() == ReservationStatus.RESERVED) {
            return existing; // idempotent
        }

        if (part.getAvailableQuantity() < quantity) {
            throw new ConflictException(
                    "PartOutOfStock : stock insuffisant pour " + part.getReference()
                            + " (disponible=" + part.getAvailableQuantity() + ", demandé=" + quantity + ")");
        }

        part.reserve(quantity);
        partRepo.save(part);

        Reservation reservation = reservationRepo.save(Reservation.builder()
                .sparePart(part)
                .maintenanceRequestId(maintenanceRequestId)
                .quantity(quantity)
                .status(ReservationStatus.RESERVED)
                .reservedAt(LocalDateTime.now())
                .build());

        recordMovement(part, reservation, MovementType.RESERVE, quantity, "MAINT-" + maintenanceRequestId);
        return reservation;
    }

    @Transactional
    public Reservation releaseReservation(Long reservationId) {
        Reservation reservation = findReservationById(reservationId);
        if (reservation.getStatus() == ReservationStatus.RELEASED) {
            return reservation; // idempotent
        }
        if (reservation.getStatus() == ReservationStatus.CONSUMED) {
            throw new ConflictException("Une réservation déjà consommée ne peut plus être libérée");
        }
        SparePart part = reservation.getSparePart();
        part.release(reservation.getQuantity());
        partRepo.save(part);

        reservation.setStatus(ReservationStatus.RELEASED);
        Reservation saved = reservationRepo.save(reservation);
        recordMovement(part, saved, MovementType.RELEASE, reservation.getQuantity(), "MAINT-" + reservation.getMaintenanceRequestId());
        return saved;
    }

    @Transactional
    public Reservation releaseByMaintenance(Long maintenanceRequestId, String partReference) {
        SparePart part = findByReference(partReference);
        Reservation reservation = reservationRepo.findByMaintenanceRequestIdAndSparePartId(maintenanceRequestId, part.getId())
                .orElseThrow(() -> new NotFoundException("Réservation introuvable pour maintenance=" + maintenanceRequestId + ", pièce=" + partReference));
        return releaseReservation(reservation.getId());
    }

    @Transactional
    public Reservation confirmUsage(Long reservationId) {
        Reservation reservation = findReservationById(reservationId);
        if (reservation.getStatus() == ReservationStatus.CONSUMED) {
            return reservation; // idempotent
        }
        if (reservation.getStatus() == ReservationStatus.RELEASED) {
            throw new ConflictException("Une réservation libérée ne peut plus être consommée");
        }
        SparePart part = reservation.getSparePart();
        part.confirmUsage(reservation.getQuantity());
        partRepo.save(part);

        reservation.setStatus(ReservationStatus.CONSUMED);
        reservation.setConfirmedAt(LocalDateTime.now());
        Reservation saved = reservationRepo.save(reservation);
        recordMovement(part, saved, MovementType.EXIT, reservation.getQuantity(), "MAINT-" + reservation.getMaintenanceRequestId());
        return saved;
    }

    @Transactional
    public Reservation confirmUsageByMaintenance(Long maintenanceRequestId, String partReference) {
        SparePart part = findByReference(partReference);
        Reservation reservation = reservationRepo.findByMaintenanceRequestIdAndSparePartId(maintenanceRequestId, part.getId())
                .orElseThrow(() -> new NotFoundException("Réservation introuvable pour maintenance=" + maintenanceRequestId + ", pièce=" + partReference));
        return confirmUsage(reservation.getId());
    }

    public List<Reservation> findAllReservations(Long maintenanceRequestId, ReservationStatus status) {
        if (maintenanceRequestId != null && status != null) {
            return reservationRepo.findByMaintenanceRequestIdAndStatusOrderByReservedAtDesc(maintenanceRequestId, status);
        }
        if (maintenanceRequestId != null) {
            return reservationRepo.findByMaintenanceRequestIdOrderByReservedAtDesc(maintenanceRequestId);
        }
        if (status != null) {
            return reservationRepo.findByStatusOrderByReservedAtDesc(status);
        }
        return reservationRepo.findAll();
    }

    public Reservation findReservationById(Long id) {
        return reservationRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Réservation introuvable avec id : " + id));
    }

    public List<Reservation> getPartReservations(Long partId) {
        findById(partId);
        return reservationRepo.findBySparePartIdOrderByReservedAtDesc(partId);
    }

    public List<StockMovement> getPartMovements(Long partId) {
        findById(partId);
        return movementRepo.findBySparePartIdOrderByMovementDateDesc(partId);
    }

    public List<StockMovement> getReservationMovements(Long reservationId) {
        findReservationById(reservationId);
        return movementRepo.findByReservationIdOrderByMovementDateDesc(reservationId);
    }

    // ---------- Statistiques & réapprovisionnement ----------
    public List<RestockSuggestion> getRestockSuggestions() {
        return partRepo.findAll().stream()
                .filter(p -> p.getAvailableQuantity() <= p.getMinThreshold())
                .map(p -> {
                    int targetMax = p.getMaxCapacity() != null ? p.getMaxCapacity() : Math.max(p.getMinThreshold() * 2, p.getQuantityInStock());
                    int orderQty = Math.max(0, targetMax - p.getQuantityInStock());
                    BigDecimal estCost = p.getUnitPrice().multiply(BigDecimal.valueOf(orderQty));
                    return new RestockSuggestion(
                            p.getId(),
                            p.getReference(),
                            p.getName(),
                            p.getStatus(),
                            p.getQuantityInStock(),
                            p.getReservedQuantity(),
                            p.getAvailableQuantity(),
                            p.getMinThreshold(),
                            p.getMaxCapacity(),
                            orderQty,
                            p.getSupplier() != null ? p.getSupplier().getName() : null,
                            p.getSupplier() != null ? p.getSupplier().getLeadTimeDays() : null,
                            estCost
                    );
                })
                .toList();
    }

    public StockValuation getValuation() {
        List<SparePart> parts = partRepo.findAll();
        int totalUnits = parts.stream().mapToInt(SparePart::getQuantityInStock).sum();
        int totalReserved = parts.stream().mapToInt(SparePart::getReservedQuantity).sum();
        BigDecimal totalValue = parts.stream()
                .map(SparePart::getStockValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, BigDecimal> byCategory = parts.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategory() != null ? p.getCategory().getName() : "Sans catégorie",
                        Collectors.reducing(BigDecimal.ZERO, SparePart::getStockValue, BigDecimal::add)
                ));
        return new StockValuation(parts.size(), totalUnits, totalReserved, totalValue, byCategory);
    }

    public List<MonthlyConsumptionStat> getMonthlyConsumptionStats() {
        return movementRepo.findByTypeOrderByMovementDateDesc(MovementType.EXIT).stream()
                .collect(Collectors.groupingBy(
                        m -> YearMonth.from(m.getMovementDate()).toString() + "|" + m.getSparePart().getReference(),
                        Collectors.summingInt(StockMovement::getQuantity)
                ))
                .entrySet().stream()
                .map(e -> {
                    String[] keys = e.getKey().split("\\|", 2);
                    return new MonthlyConsumptionStat(keys[0], keys[1], e.getValue());
                })
                .sorted(Comparator.comparing(MonthlyConsumptionStat::month).reversed()
                        .thenComparing(MonthlyConsumptionStat::partReference))
                .toList();
    }

    // ---------- Helpers ----------
    private PartCategory resolveCategory(Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepo.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Catégorie introuvable avec id : " + categoryId));
    }

    private Supplier resolveSupplier(Long supplierId) {
        if (supplierId == null) {
            return null;
        }
        return supplierRepo.findById(supplierId)
                .orElseThrow(() -> new NotFoundException("Fournisseur introuvable avec id : " + supplierId));
    }

    private void validateCapacityBounds(Integer quantityInStock, Integer minThreshold, Integer maxCapacity) {
        if (minThreshold != null && maxCapacity != null && minThreshold > maxCapacity) {
            throw new IllegalArgumentException("Le seuil minimal ne peut pas dépasser la capacité maximale");
        }
        if (quantityInStock != null && maxCapacity != null && quantityInStock > maxCapacity) {
            throw new IllegalArgumentException("La quantité en stock ne peut pas dépasser la capacité maximale (" + maxCapacity + ")");
        }
    }

    private void recordMovement(SparePart part, Reservation reservation, MovementType type, int quantity, String reference) {
        movementRepo.save(StockMovement.builder()
                .sparePart(part)
                .reservation(reservation)
                .type(type)
                .quantity(quantity)
                .movementDate(LocalDateTime.now())
                .reference(reference)
                .build());
    }
}
