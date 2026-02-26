package defacement.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import defacement.service.ScanOrchestratorService;

import java.util.concurrent.atomic.AtomicBoolean;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefacementScheduler {

    private final ScanOrchestratorService scanOrchestratorService;

    // Prevent overlapping runs
    private final AtomicBoolean running = new AtomicBoolean(false);

    @Scheduled(cron = "0 */1 * * * *")
    public void runScheduledScan() {

        if (!running.compareAndSet(false, true)) {
            log.warn("Scan already running, skipping this cycle");
            return;
        }

        try {
            scanOrchestratorService.runFullScan();
        } catch (Exception ex) {
            log.error("Unexpected error during scan cycle", ex);
        } finally {
            running.set(false);
        }
    }
}
