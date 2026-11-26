package com.aquasentinel.servicefacturation.controller;

import com.aquasentinel.servicefacturation.entity.WaterInvoice;
import com.aquasentinel.servicefacturation.enums.InvoiceStatus;
import com.aquasentinel.servicefacturation.service.WaterInvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class WaterInvoiceController {

    private final WaterInvoiceService service;

    public WaterInvoiceController(WaterInvoiceService service) {
        this.service = service;
    }

    @GetMapping
    public List<WaterInvoice> getAll(@RequestParam(required = false) Long buildingId,
                                     @RequestParam(required = false) InvoiceStatus status) {
        return service.findAll(buildingId, status);
    }

    @GetMapping("/{id}")
    public WaterInvoice getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public WaterInvoice create(@RequestBody WaterInvoice invoice) {
        return service.create(invoice);
    }

    @PutMapping("/{id}")
    public WaterInvoice update(@PathVariable Long id, @RequestBody WaterInvoice invoice) {
        return service.update(id, invoice);
    }

    @PatchMapping("/{id}/status")
    public WaterInvoice updateStatus(@PathVariable Long id, @RequestParam("value") InvoiceStatus status) {
        return service.updateStatus(id, status);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}