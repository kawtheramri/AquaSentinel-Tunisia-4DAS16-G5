package com.aquasentinel.servicepiecesrechange;

import com.aquasentinel.servicepiecesrechange.exception.ConflictException;
import com.aquasentinel.servicepiecesrechange.model.*;
import com.aquasentinel.servicepiecesrechange.repository.PartCategoryRepository;
import com.aquasentinel.servicepiecesrechange.repository.SupplierRepository;
import com.aquasentinel.servicepiecesrechange.service.SparePartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class ServicePiecesRechangeApplicationTests {

    @Autowired
    private SparePartService service;

    @Autowired
    private PartCategoryRepository categoryRepo;

    @Autowired
    private SupplierRepository supplierRepo;

    @Test
    void contextLoadsAndSeededPartsExist() {
        assertThat(service.findAll(null, null, null, null)).hasSizeGreaterThanOrEqualTo(4);
    }

    @Test
    void crudAndReservationLifecycleWork() {
        PartCategory cat = categoryRepo.save(PartCategory.builder().name("Raccords Test").build());
        Supplier sup = supplierRepo.save(Supplier.builder().name("Fournisseur Test").leadTimeDays(3).build());

        SparePart created = service.create(new SparePartService.SparePartInput(
                "RAC-TEST-99",
                "Raccord laiton 20mm",
                "Raccord fileté test",
                "pièce",
                10,
                3,
                50,
                new BigDecimal("8.50"),
                "Z1-01",
                cat.getId(),
                sup.getId()
        ));
        assertThat(created.getId()).isNotNull();
        assertThat(created.getStatus()).isEqualTo(PartStatus.AVAILABLE);
        assertThat(created.getCategory().getName()).isEqualTo("Raccords Test");
        assertThat(created.getSupplier().getName()).isEqualTo("Fournisseur Test");

        Reservation res = service.reserve("RAC-TEST-99", 8, 9001L);
        assertThat(res.getStatus()).isEqualTo(ReservationStatus.RESERVED);
        SparePart afterReserve = service.findById(created.getId());
        assertThat(afterReserve.getReservedQuantity()).isEqualTo(8);
        assertThat(afterReserve.getAvailableQuantity()).isEqualTo(2);
        assertThat(afterReserve.getStatus()).isEqualTo(PartStatus.LOW_STOCK);

        assertThatThrownBy(() -> service.reserve("RAC-TEST-99", 5, 9002L))
                .isInstanceOf(ConflictException.class);

        Reservation consumed = service.confirmUsage(res.getId());
        assertThat(consumed.getStatus()).isEqualTo(ReservationStatus.CONSUMED);
        SparePart afterConsume = service.findById(created.getId());
        assertThat(afterConsume.getQuantityInStock()).isEqualTo(2);
        assertThat(afterConsume.getReservedQuantity()).isEqualTo(0);

        Reservation res2 = service.reserve("RAC-TEST-99", 2, 9003L);
        Reservation released = service.releaseReservation(res2.getId());
        assertThat(released.getStatus()).isEqualTo(ReservationStatus.RELEASED);
        assertThat(service.findById(created.getId()).getAvailableQuantity()).isEqualTo(2);

        service.delete(created.getId());
    }
}
