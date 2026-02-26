package defacement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import defacement.dto.DashboardSummaryView;
import defacement.dto.TargetDashboardView;
import defacement.model.MonitorTarget;
import defacement.model.ScanJob;
import defacement.repository.MonitorTargetRepository;
import defacement.repository.ScanJobRepository;
import defacement.repository.ScanResultRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final MonitorTargetRepository targetRepository;
    private final ScanJobRepository scanJobRepository;
    private final ScanResultRepository scanResultRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TargetDashboardView> getAllTargetStatuses() {
        return targetRepository.findByDeletedAtIsNull()
                .stream()
                .map(target -> {
                    // Latest scan job
                    ScanJob lastJob = scanJobRepository
                            .findTopByTargetOrderByStartedAtDesc(target)
                            .orElse(null);

                    // Default values
                    String status = "N/A";
                    List<String> failedIndicators = List.of();
                    int failedCount = 0;

                    if (lastJob != null) {
                        // Determine status
                        switch (lastJob.getStatus()) {
                            case "SUCCESS" -> status = "OK";
                            case "DEFACED" -> status = "DEFACED";
                            case "FAILED" -> status = "ERROR";
                            default -> status = lastJob.getStatus();
                        }

                        // Collect failed indicators from **this last scan only**
                        failedIndicators = lastJob.getScanResults()
                                .stream()
                                .filter(sr -> "DEFACED".equals(sr.getStatus()))
                                .filter(sr -> sr.getIndicator() != null && sr.getIndicator().isEnabled())
                                .map(sr -> sr.getIndicator().getValue())
                                .distinct()
                                .toList();

                        failedCount = failedIndicators.size();
                    }

                    return new TargetDashboardView(
                            target.getId(),
                            target.getName(),
                            target.getBaseUrl(),
                            status,
                            lastJob != null ? lastJob.getStartedAt() : null,
                            failedCount,
                            failedIndicators
                    );
                })
                .toList();
    }


    @Override
    public DashboardSummaryView getDashboardSummary() {

        var targets = targetRepository.findByDeletedAtIsNull();

        long totalTargets = targets.size();
        long healthy = 0;
        long defaced = 0;
        long failed = 0;
        long activeAlerts = 0;

        for (MonitorTarget target : targets) {

            ScanJob lastJob = scanJobRepository
                    .findTopByTargetOrderByStartedAtDesc(target)
                    .orElse(null);

            if (lastJob == null) continue;

            switch (lastJob.getStatus()) {
                case "SUCCESS" -> healthy++;
                case "DEFACED" -> {
                    defaced++;
                    activeAlerts++;
                }
                case "FAILED" -> failed++;
            }
        }

        return new DashboardSummaryView(
                totalTargets,
                healthy,
                defaced,
                failed,
                activeAlerts
        );
    }

    @Transactional(readOnly = true)
    public List<String> getDefacedIndicators() {

        return targetRepository.findByDeletedAtIsNull()
                .stream()
                .map(target -> scanJobRepository
                        .findTopByTargetOrderByStartedAtDesc(target)
                        .orElse(null))
                .filter(job -> job != null && "DEFACED".equals(job.getStatus()))
                .flatMap(job -> job.getScanResults().stream())
                .filter(result -> "DEFACED".equals(result.getStatus()))
                .filter(result -> result.getIndicator().isEnabled())
                .map(result -> result.getIndicator().getValue())
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> getDefacedIndicatorsList() {

        return targetRepository.findByDeletedAtIsNull()
                .stream()
                // get latest scan job per target
                .map(target -> scanJobRepository
                        .findTopByTargetOrderByStartedAtDesc(target)
                        .orElse(null))
                // only current DEFACED targets
                .filter(job -> job != null && "DEFACED".equals(job.getStatus()))
                // get results from that job
                .flatMap(job -> job.getScanResults().stream())
                // only DEFACED indicator results
                .filter(result -> "DEFACED".equals(result.getStatus()))
                // only enabled indicators
                .filter(result -> result.getIndicator() != null
                        && result.getIndicator().isEnabled())
                .map(result -> result.getIndicator().getValue())
                .distinct()
                .toList();
    }
}