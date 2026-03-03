package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import defacement.model.MonitorTarget;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface MonitorTargetRepository extends JpaRepository<MonitorTarget, Long> {

    List<MonitorTarget> findByDeletedAtIsNull();

    List<MonitorTarget> findByScanEnabledTrueAndDeletedAtIsNull();
}