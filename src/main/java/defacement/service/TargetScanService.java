package defacement.service;

import defacement.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import defacement.detection.IndicatorDetectionService;
import defacement.dto.DetectionResult;
import defacement.fetcher.ContentFetcher;
import defacement.model.*;
import defacement.repository.ScanJobRepository;
import defacement.repository.ScanResultRepository;
import defacement.repository.TargetIndicatorRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TargetScanService {

    private final ScanJobRepository scanJobRepository;
    private final TargetIndicatorRepository targetIndicatorRepository;
    private final ScanResultRepository scanResultRepository;
    private final ContentFetcher contentFetcher;
    private final IndicatorDetectionService detectionService;
    private final AlertService alertService;

    @Transactional(rollbackFor = Exception.class)
    public void scanTarget(MonitorTarget target) {

        ScanJob scanJob = createScanJob(target);
        boolean defacementDetected = false;

        try {
            String content = contentFetcher.fetch(target.getBaseUrl());

            List<TargetIndicator> mappings =
                    targetIndicatorRepository.findByTargetAndDeletedAtIsNull(target);

            if (mappings.isEmpty()) {
                log.warn("No indicators assigned to target [{}] - skipping scan", target.getBaseUrl());
            }

            for (TargetIndicator mapping : mappings) {

                DefacementIndicator indicator = mapping.getIndicator();

                if (indicator == null || !indicator.isEnabled()) {
                    log.debug("Skipping disabled or null indicator on target [{}]", target.getBaseUrl());
                    continue;
                }

                DetectionResult result = detectionService.detect(indicator, content);
                saveResult(scanJob, indicator, result);

                if (!result.isFound()) {
                    defacementDetected = true;
                    log.warn("DEFACEMENT DETECTED - Target [{}] | Indicator [{}] (type: {})",
                            target.getBaseUrl(),
                            indicator.getValue(),
                            indicator.getType());
                }
            }

            finalizeJob(scanJob, defacementDetected);

        } catch (Exception ex) {
            log.error("Scan failed for target [{}]: {}", target.getBaseUrl(), ex.getMessage(), ex);
            failJob(scanJob, ex);
            throw ex;
        }
    }

    private ScanJob createScanJob(MonitorTarget target) {
        ScanJob job = new ScanJob();
        job.setTarget(target);
        job.setStatus("RUNNING");
        job.setStartedAt(LocalDateTime.now());
        ScanJob saved = scanJobRepository.save(job);
        log.info("Scan job [{}] created for target [{}]", saved.getId(), target.getBaseUrl());
        return saved;
    }

    private void saveResult(ScanJob job, DefacementIndicator indicator, DetectionResult result) {

        ScanResult entity = new ScanResult();
        entity.setScanJob(job);
        entity.setIndicator(indicator);
        entity.setFound(result.isFound());
        entity.setDetails(result.getDetails());
        entity.setCheckedAt(LocalDateTime.now());
        entity.setStatus(result.isFound() ? "SUCCESS" : "DEFACED");

        scanResultRepository.save(entity);

        log.debug("Scan result saved - job [{}] | indicator [{}] | status [{}]",
                job.getId(),
                indicator.getValue(),
                entity.getStatus());
    }

    private void finalizeJob(ScanJob job, boolean defaced) {
        job.setFinishedAt(LocalDateTime.now());
        job.setStatus(defaced ? "DEFACED" : "SUCCESS");
        scanJobRepository.save(job);

        if (defaced) {
            log.warn("Scan job [{}] finalized with status DEFACED for target [{}]",
                    job.getId(),
                    job.getTarget().getBaseUrl());
            alertService.createAlert(job);
            log.info("Alert created for scan job [{}]", job.getId());
        } else {
            log.info("Scan job [{}] finalized with status SUCCESS for target [{}]",
                    job.getId(),
                    job.getTarget().getBaseUrl());
        }
    }

    private void failJob(ScanJob job, Exception ex) {
        job.setStatus("FAILED");
        job.setErrorMessage(ex.getMessage());
        job.setFinishedAt(LocalDateTime.now());
        scanJobRepository.save(job);
        log.error("Scan job [{}] marked as FAILED for target [{}] - reason: {}",
                job.getId(),
                job.getTarget().getBaseUrl(),
                ex.getMessage());
    }
}