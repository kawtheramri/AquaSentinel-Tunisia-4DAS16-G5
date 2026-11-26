package com.aquasentinel.servicefacturation.service;

import com.aquasentinel.servicefacturation.entity.InvoiceLine;
import com.aquasentinel.servicefacturation.entity.SavingsEstimate;
import com.aquasentinel.servicefacturation.entity.TariffPlan;
import com.aquasentinel.servicefacturation.entity.WaterInvoice;
import com.aquasentinel.servicefacturation.enums.InvoiceStatus;
import com.aquasentinel.servicefacturation.exception.ResourceNotFoundException;
import com.aquasentinel.servicefacturation.repository.TariffPlanRepository;
import com.aquasentinel.servicefacturation.repository.WaterInvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WaterInvoiceService {

    private final WaterInvoiceRepository repository;
    private final TariffPlanRepository planRepository;

    public WaterInvoiceService(WaterInvoiceRepository repository,
                               TariffPlanRepository planRepository) {
        this.repository = repository;
        this.planRepository = planRepository;
    }

    public List<WaterInvoice> findAll(Long buildingId, InvoiceStatus status) {
        List<WaterInvoice> list = (buildingId != null)
                ? repository.findByBuildingId(buildingId)
                : repository.findAll();
        if (status != null) {
            list = list.stream().filter(i -> i.getStatus() == status).toList();
        }
        return list;
    }

    public WaterInvoice findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Facture introuvable : " + id));
    }

    @Transactional
    public WaterInvoice create(WaterInvoice invoice) {
        invoice.setId(null);
        invoice.setTariffPlan(resolvePlan(invoice.getTariffPlan()));
        if (invoice.getLines() != null) {
            for (InvoiceLine line : invoice.getLines()) {
                line.setId(null);
                line.setInvoice(invoice);
            }
        }
        if (invoice.getSavingsEstimates() != null) {
            for (SavingsEstimate s : invoice.getSavingsEstimates()) {
                s.setId(null);
                s.setInvoice(invoice);
            }
        }
        return repository.save(invoice);
    }

    @Transactional
    public WaterInvoice update(Long id, WaterInvoice data) {
        WaterInvoice invoice = findById(id);
        invoice.setInvoiceNumber(data.getInvoiceNumber());
        invoice.setBuildingId(data.getBuildingId());
        invoice.setTariffPlan(resolvePlan(data.getTariffPlan()));
        invoice.setPeriodStart(data.getPeriodStart());
        invoice.setPeriodEnd(data.getPeriodEnd());
        invoice.setConsumptionM3(data.getConsumptionM3());
        invoice.setQuotaM3(data.getQuotaM3());
        invoice.setBaseAmount(data.getBaseAmount());
        invoice.setSurchargeAmount(data.getSurchargeAmount());
        invoice.setFixedFees(data.getFixedFees());
        invoice.setTaxAmount(data.getTaxAmount());
        invoice.setTotalAmount(data.getTotalAmount());
        invoice.setStatus(data.getStatus());

        // Les listes sont remplacées seulement si elles sont fournies dans la requête
        if (data.getLines() != null) {
            invoice.getLines().clear();
            for (InvoiceLine line : data.getLines()) {
                line.setId(null);
                line.setInvoice(invoice);
                invoice.getLines().add(line);
            }
        }
        if (data.getSavingsEstimates() != null) {
            invoice.getSavingsEstimates().clear();
            for (SavingsEstimate s : data.getSavingsEstimates()) {
                s.setId(null);
                s.setInvoice(invoice);
                invoice.getSavingsEstimates().add(s);
            }
        }
        return repository.save(invoice);
    }

    @Transactional
    public WaterInvoice updateStatus(Long id, InvoiceStatus status) {
        WaterInvoice invoice = findById(id);
        invoice.setStatus(status);
        return repository.save(invoice);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(findById(id));
    }

    private TariffPlan resolvePlan(TariffPlan ref) {
        if (ref == null || ref.getId() == null) {
            throw new IllegalArgumentException("tariffPlan.id est obligatoire");
        }
        return planRepository.findById(ref.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plan tarifaire introuvable : " + ref.getId()));
    }
}