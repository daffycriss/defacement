package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import defacement.model.MonitorTarget;

import java.util.List;

public interface MonitorTargetRepository extends JpaRepository<MonitorTarget, Long> {

    List<MonitorTarget> findByDeletedAtIsNull();

    List<MonitorTarget> findByScanEnabledTrueAndDeletedAtIsNull();
}