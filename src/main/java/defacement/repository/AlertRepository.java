package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import defacement.model.Alert;
import defacement.model.MonitorTarget;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    // Existing method
    boolean existsByTargetAndAcknowledgedFalse(MonitorTarget target);

    // New method for dashboard
    int countByTargetAndAcknowledgedFalse(MonitorTarget target);
}