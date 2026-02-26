package defacement.service;

import defacement.model.MonitorTarget;

import java.util.List;
import java.util.Optional;

public interface MonitorTargetService {
    List<MonitorTarget> getAllActiveTargets();
    List<MonitorTarget> getAllTargets();
    Optional<MonitorTarget> getById(Long id);
    MonitorTarget save(MonitorTarget target);
    void softDelete(Long id);
}