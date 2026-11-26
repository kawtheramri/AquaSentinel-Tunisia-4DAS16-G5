package com.aquasentinel.servicepiecesrechange.config;

import com.aquasentinel.servicepiecesrechange.model.*;
import com.aquasentinel.servicepiecesrechange.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedSpareParts(
            PartCategoryRepository categoryRepo,
            SupplierRepository supplierRepo,
            SparePartRepository partRepo,
            StockMovementRepository movementRepo
    ) {
        return args -> {
            if (partRepo.count() > 0) {
                return;
            }
            PartCategory joints = categoryRepo.save(PartCategory.builder()
                    .name("Joints")
                    .description("Joints toriques et d'étanchéité")
                    .build());
            PartCategory vannes = categoryRepo.save(PartCategory.builder()
                    .name("Vannes")
                    .description("Vannes d'arrêt et clapets anti-retour")
                    .build());
            PartCategory tuyaux = categoryRepo.save(PartCategory.builder()
                    .name("Tuyaux")
                    .description("Conduites PEHD, PVC et cuivre")
                    .build());
            PartCategory compteurs = categoryRepo.save(PartCategory.builder()
                    .name("Compteurs")
                    .description("Compteurs d'eau intelligents et raccords")
                    .build());

            Supplier hydro = supplierRepo.save(Supplier.builder()
                    .name("HydroTunisie")
                    .phone("+216 71 100 200")
                    .email("contact@hydrotunisie.tn")
                    .leadTimeDays(2)
                    .build());
            Supplier aquaPro = supplierRepo.save(Supplier.builder()
                    .name("AquaPro")
                    .phone("+216 71 300 400")
                    .email("commandes@aquapro.tn")
                    .leadTimeDays(5)
                    .build());
            Supplier smartWater = supplierRepo.save(Supplier.builder()
                    .name("SmartWater TN")
                    .phone("+216 70 500 600")
                    .email("sales@smartwater.tn")
                    .leadTimeDays(7)
                    .build());

            LocalDateTime now = LocalDateTime.now();
            List<SparePart> savedParts = partRepo.saveAll(List.of(
                    SparePart.builder()
                            .reference("JNT-20MM")
                            .name("Joint torique EPDM 20mm")
                            .description("Joint d'étanchéité haute pression pour raccord 20mm")
                            .unit("pièce")
                            .quantityInStock(40)
                            .reservedQuantity(0)
                            .minThreshold(10)
                            .maxCapacity(100)
                            .unitPrice(new BigDecimal("3.50"))
                            .storageLocation("A1-02")
                            .category(joints)
                            .supplier(hydro)
                            .lastRestockedAt(now)
                            .build(),
                    SparePart.builder()
                            .reference("VAN-DN25")
                            .name("Vanne d'arrêt laiton DN25")
                            .description("Vanne à boisseau sphérique DN25")
                            .unit("pièce")
                            .quantityInStock(5)
                            .reservedQuantity(0)
                            .minThreshold(6)
                            .maxCapacity(30)
                            .unitPrice(new BigDecimal("45.00"))
                            .storageLocation("B2-04")
                            .category(vannes)
                            .supplier(aquaPro)
                            .lastRestockedAt(now)
                            .build(),
                    SparePart.builder()
                            .reference("TUY-PE32")
                            .name("Tuyau PEHD 32mm")
                            .description("Conduite polyéthylène haute densité 32mm PN16")
                            .unit("mètre")
                            .quantityInStock(0)
                            .reservedQuantity(0)
                            .minThreshold(15)
                            .maxCapacity(120)
                            .unitPrice(new BigDecimal("12.00"))
                            .storageLocation("C1-01")
                            .category(tuyaux)
                            .supplier(aquaPro)
                            .build(),
                    SparePart.builder()
                            .reference("CPT-SMART15")
                            .name("Compteur d'eau intelligent DN15")
                            .description("Compteur volumétrique avec émetteur d'impulsions")
                            .unit("pièce")
                            .quantityInStock(12)
                            .reservedQuantity(0)
                            .minThreshold(4)
                            .maxCapacity(25)
                            .unitPrice(new BigDecimal("185.00"))
                            .storageLocation("D3-01")
                            .category(compteurs)
                            .supplier(smartWater)
                            .lastRestockedAt(now)
                            .build()
            ));

            for (SparePart p : savedParts) {
                if (p.getQuantityInStock() > 0) {
                    movementRepo.save(StockMovement.builder()
                            .sparePart(p)
                            .type(MovementType.ENTRY)
                            .quantity(p.getQuantityInStock())
                            .movementDate(now)
                            .reference("INIT-" + p.getReference())
                            .build());
                }
            }
        };
    }
}
