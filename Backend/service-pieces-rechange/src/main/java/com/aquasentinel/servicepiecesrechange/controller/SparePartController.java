package com.aquasentinel.servicepiecesrechange.controller;

import com.aquasentinel.servicepiecesrechange.model.PartStatus;
import com.aquasentinel.servicepiecesrechange.model.Reservation;
import com.aquasentinel.servicepiecesrechange.model.SparePart;
import com.aquasentinel.servicepiecesrechange.model.StockMovement;
import com.aquasentinel.servicepiecesrechange.service.SparePartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/parts")
@RequiredArgsConstructor
@Tag(name = "Spare Parts", description = "Catalogue, stock, réservations, mouvements et statistiques des pièces")
public class SparePartController {

    private final SparePartService service;

    public record SparePartRequest(
            @NotBlank(message = "La référence est obligatoire") String reference,
            @NotBlank(message = "Le nom de la pièce est obligatoire") String name,
            String description,
            String unit,
            @NotNull @Min(0) Integer quantityInStock,
            @NotNull @Min(0) Integer minThreshold,
            @Min(1) Integer maxCapacity,
            @NotNull @DecimalMin("0.0") BigDecimal unitPrice,
            String storageLocation,
            Long categoryId,
            Long supplierId
    ) {
        SparePartService.SparePartInput toInput() {
            return new SparePartService.SparePartInput(
                    reference, name, description, unit,
                    quantityInStock, minThreshold, maxCapacity,
                    unitPrice, storageLocation, categoryId, supplierId
            );
        }
    }

    public record StockAdjustmentRequest(int delta, String reference) {}

    public record StockReservationRequest(
            @NotBlank(message = "La référence de pièce est obligatoire") String partReference,
            @Min(value = 1, message = "La quantité doit être au moins 1") int quantity,
            @NotNull(message = "maintenanceRequestId est obligatoire") Long maintenanceRequestId
    ) {}

    public record MaintenanceActionRequest(
            @NotBlank(message = "La référence de pièce est obligatoire") String partReference,
            @NotNull(message = "maintenanceRequestId est obligatoire") Long maintenanceRequestId
    ) {}

    @Operation(summary = "Lister ou filtrer les pièces", description = "Filtre optionnel par categoryId, supplierId, status ou recherche texte.")
    @GetMapping
    public List<SparePart> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long supplierId,
            @RequestParam(required = false) PartStatus status,
            @RequestParam(required = false) String search
    ) {
        return service.findAll(categoryId, supplierId, status, search);
    }

    @Operation(summary = "Consulter une pièce par ID")
    @GetMapping("/{id}")
    public SparePart getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @Operation(summary = "Consulter une pièce par référence")
    @GetMapping("/reference/{reference}")
    public SparePart getByReference(@PathVariable String reference) {
        return service.findByReference(reference);
    }

    @Operation(summary = "Ajouter une pièce au catalogue (liée à PartCategory et Supplier)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SparePart create(@Valid @RequestBody SparePartRequest req) {
        return service.create(req.toInput());
    }

    @Operation(summary = "Modifier une pièce existante")
    @PutMapping("/{id}")
    public SparePart update(@PathVariable Long id, @Valid @RequestBody SparePartRequest req) {
        return service.update(id, req.toInput());
    }

    @Operation(summary = "Supprimer une pièce")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @Operation(summary = "Ajuster manuellement le stock (ENTRY / EXIT)")
    @PatchMapping("/{id}/adjust")
    public SparePart adjustStock(@PathVariable Long id, @RequestBody StockAdjustmentRequest req) {
        return service.adjustStock(id, req.delta(), req.reference());
    }

    @Operation(summary = "Réserver une pièce pour une intervention (appelé par Maintenance)")
    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    public Reservation reserve(@Valid @RequestBody StockReservationRequest req) {
        return service.reserve(req.partReference(), req.quantity(), req.maintenanceRequestId());
    }

    @Operation(summary = "Libérer une réservation par maintenanceRequestId + partReference")
    @PostMapping("/release")
    public Reservation release(@Valid @RequestBody MaintenanceActionRequest req) {
        return service.releaseByMaintenance(req.maintenanceRequestId(), req.partReference());
    }

    @Operation(summary = "Confirmer la consommation (sortie définitive) par maintenanceRequestId + partReference")
    @PostMapping("/confirm-usage")
    public Reservation confirmUsage(@Valid @RequestBody MaintenanceActionRequest req) {
        return service.confirmUsageByMaintenance(req.maintenanceRequestId(), req.partReference());
    }

    @Operation(summary = "Lister les réservations d'une pièce")
    @GetMapping("/{id}/reservations")
    public List<Reservation> reservations(@PathVariable Long id) {
        return service.getPartReservations(id);
    }

    @Operation(summary = "Historique des mouvements de stock d'une pièce")
    @GetMapping("/{id}/movements")
    public List<StockMovement> movements(@PathVariable Long id) {
        return service.getPartMovements(id);
    }

    @Operation(summary = "Suggestions de réapprovisionnement jusqu'à maxCapacity")
    @GetMapping("/restock-suggestions")
    public List<SparePartService.RestockSuggestion> restockSuggestions() {
        return service.getRestockSuggestions();
    }

    @Operation(summary = "Valorisation totale et par catégorie du stock")
    @GetMapping("/valuation")
    public SparePartService.StockValuation valuation() {
        return service.getValuation();
    }

    @Operation(summary = "Statistiques de consommation par pièce et par mois")
    @GetMapping("/stats/monthly")
    public List<SparePartService.MonthlyConsumptionStat> monthlyStats() {
        return service.getMonthlyConsumptionStats();
    }
}
