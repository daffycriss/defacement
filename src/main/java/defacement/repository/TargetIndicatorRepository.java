package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import defacement.model.DefacementIndicator;
import defacement.model.MonitorTarget;
import defacement.model.TargetIndicator;

import java.util.List;

public interface TargetIndicatorRepository
        extends JpaRepository<TargetIndicator, Long> {

    List<TargetIndicator> findByTargetAndDeletedAtIsNull(MonitorTarget target);

    // Check if a mapping already exists between a target and an indicator
    boolean existsByTargetAndIndicator(MonitorTarget target, DefacementIndicator indicator);

    @Query("SELECT ti FROM TargetIndicator ti JOIN FETCH ti.indicator WHERE ti.target = :target")
    List<TargetIndicator> findByTarget(@Param("target") MonitorTarget target);

    boolean existsByIndicator(DefacementIndicator indicator);
}
