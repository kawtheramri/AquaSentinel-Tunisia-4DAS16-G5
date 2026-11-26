package com.aquasentinel.servicefacturation.repository;

import com.aquasentinel.servicefacturation.entity.WaterInvoice;
import com.aquasentinel.servicefacturation.enums.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WaterInvoiceRepository extends JpaRepository<WaterInvoice, Long> {

    List<WaterInvoice> findByBuildingId(Long buildingId);

    List<WaterInvoice> findByStatus(InvoiceStatus status);

    boolean existsByInvoiceNumber(String invoiceNumber);
}