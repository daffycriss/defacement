package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import defacement.model.ScanJob;
import defacement.model.MonitorTarget;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@EnableJpaRepositories
public interface ScanJobRepository extends JpaRepository<ScanJob, Long> {

    Optional<ScanJob> findTopByTargetOrderByStartedAtDesc(MonitorTarget target);

}
