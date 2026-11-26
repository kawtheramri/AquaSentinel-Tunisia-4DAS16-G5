package com.aquasentinel.servicefacturation.repository;

import com.aquasentinel.servicefacturation.entity.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long> {
}