package defacement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import defacement.model.Alert;
import defacement.model.MonitorTarget;
import defacement.model.ScanJob;
import defacement.repository.AlertRepository;

@Service
@RequiredArgsConstructor
public class AlertService {

    private final AlertRepository alertRepository;

    public void createAlert(ScanJob scanJob) {

        MonitorTarget target = scanJob.getTarget();

        if (alertRepository.existsByTargetAndAcknowledgedFalse(target)) {
            return; // avoid alert spam
        }

        Alert alert = new Alert();
        alert.setTarget(target);
        alert.setScanJob(scanJob);
        alert.setSeverity("CRITICAL");
        alert.setMessage("Defacement detected on " + target.getBaseUrl());
        alertRepository.save(alert);
    }
}
