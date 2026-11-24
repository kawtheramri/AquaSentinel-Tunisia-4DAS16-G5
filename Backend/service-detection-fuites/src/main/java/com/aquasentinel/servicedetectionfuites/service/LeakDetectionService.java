package com.aquasentinel.servicedetectionfuites.service;

import com.aquasentinel.servicedetectionfuites.exception.ConflictException;
import com.aquasentinel.servicedetectionfuites.exception.NotFoundException;
import com.aquasentinel.servicedetectionfuites.model.*;
import com.aquasentinel.servicedetectionfuites.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LeakDetectionService {

    private final LeakDetectionRepository leakRepo;
    private final DetectionRuleRepository ruleRepo;
    private final LeakVerificationRepository verificationRepo;
    private final LeakStatusHistoryRepository historyRepo;

    // ---------- CRUD ----------
    public List<LeakDetection> findAll() {
        return leakRepo.findAll();
    }

    public LeakDetection findById(String id) {
        return leakRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Détection introuvable : " + id));
    }

    public List<LeakDetection> findByBuilding(Long buildingId) {
        return leakRepo.findByBuildingId(buildingId);
    }

    public LeakDetection create(LeakDetection leak) {
        if (leak.getMeasurementId() != null && leakRepo.existsByMeasurementId(leak.getMeasurementId())) {
            throw new ConflictException("Ce relevé a déjà été analysé : " + leak.getMeasurementId());
        }
        if (leak.getMethod() == null) {
            throw new IllegalArgumentException("La méthode de détection est obligatoire");
        }
        // association logique avec la règle active de cette méthode
        ruleRepo.findFirstByMethodAndActiveTrue(leak.getMethod())
                .ifPresent(rule -> leak.setRuleId(rule.getId()));

        leak.setId(null);
        leak.setDetectedAt(LocalDateTime.now());
        leak.analyze();                       // calcule perte, niveau de risque, statut NEW
        LeakDetection saved = leakRepo.save(leak);
        addHistory(saved.getId(), null, saved.getStatus(), "Détection créée");
        return saved;
    }

    public LeakDetection update(String id, LeakDetection data) {
        LeakDetection leak = findById(id);
        if (leak.getStatus() == DetectionStatus.RESOLVED) {
            throw new ConflictException("Une fuite résolue ne peut plus être modifiée");
        }
        leak.setBuildingId(data.getBuildingId());
        leak.setMethod(data.getMethod());
        leak.setMeasuredValue(data.getMeasuredValue());
        leak.setExpectedValue(data.getExpectedValue());
        leak.setSimulated(data.isSimulated());
        leak.analyze();                       // recalcule le risque
        return leakRepo.save(leak);
    }

    public void delete(String id) {
        findById(id);                         // 404 si inexistant
        verificationRepo.deleteByLeakDetectionId(id);   // suppression en cascade
        historyRepo.deleteByLeakDetectionId(id);
        leakRepo.deleteById(id);
    }

    // ---------- Actions métier ----------
    public LeakDetection verify(String id, Long verifiedBy, String comment) {
        LeakDetection leak = findById(id);
        if (leak.getStatus() != DetectionStatus.NEW) {
            throw new ConflictException("Seule une détection NEW peut être vérifiée");
        }
        return changeStatus(leak, DetectionStatus.VERIFIED, verifiedBy, comment);
    }

    public LeakDetection markFalsePositive(String id, Long verifiedBy, String comment) {
        LeakDetection leak = findById(id);
        if (leak.getStatus() == DetectionStatus.RESOLVED) {
            throw new ConflictException("Une fuite résolue ne peut pas être un faux positif");
        }
        return changeStatus(leak, DetectionStatus.FALSE_POSITIVE, verifiedBy, comment);
    }

    public List<LeakVerification> verifications(String id) {
        findById(id);
        return verificationRepo.findByLeakDetectionId(id);
    }

    public List<LeakStatusHistory> history(String id) {
        findById(id);
        return historyRepo.findByLeakDetectionIdOrderByChangedAtAsc(id);
    }

    // ---------- Méthodes internes ----------
    private LeakDetection changeStatus(LeakDetection leak, DetectionStatus newStatus,
                                       Long verifiedBy, String comment) {
        DetectionStatus old = leak.getStatus();
        leak.setStatus(newStatus);
        LeakDetection saved = leakRepo.save(leak);

        verificationRepo.save(LeakVerification.builder()
                .leakDetectionId(saved.getId())
                .verifiedBy(verifiedBy)
                .decision(newStatus)
                .comment(comment)
                .verifiedAt(LocalDateTime.now())
                .build());

        addHistory(saved.getId(), old, newStatus, comment);
        return saved;
    }

    private void addHistory(String leakId, DetectionStatus oldS, DetectionStatus newS, String reason) {
        historyRepo.save(LeakStatusHistory.builder()
                .leakDetectionId(leakId)
                .oldStatus(oldS)
                .newStatus(newS)
                .changedAt(LocalDateTime.now())
                .reason(reason)
                .build());
    }
}
