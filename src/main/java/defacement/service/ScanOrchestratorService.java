package defacement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import defacement.dto.ConnectivityCheckResult;
import defacement.model.MonitorTarget;
import defacement.repository.MonitorTargetRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScanOrchestratorService {

    private final ConnectivityService connectivityService;
    private final MonitorTargetRepository targetRepository;
    private final TargetScanService targetScanService;

    @Transactional
    public void runFullScan() {

        log.info("Starting defacement scan cycle");

        ConnectivityCheckResult connectivity = connectivityService.checkInternet();

        if (!connectivity.isAvailable()) {
            log.warn("No internet connection. Scan aborted.");
            return;
        }

        List<MonitorTarget> targets =
                targetRepository.findByScanEnabledTrueAndDeletedAtIsNull();

        if (targets.isEmpty()) {
            log.info("No active targets to scan");
            return;
        }

        for (MonitorTarget target : targets) {
            try {
                targetScanService.scanTarget(target);
            } catch (Exception ex) {
                log.error("Target scan failed for {}", target.getBaseUrl(), ex);
            }
        }

        log.info("Defacement scan cycle completed");
    }
}
