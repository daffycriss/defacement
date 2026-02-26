package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import defacement.model.DefacementIndicator;

import java.util.List;

public interface DefacementIndicatorRepository
        extends JpaRepository<DefacementIndicator, Long> {

    List<DefacementIndicator> findByEnabledTrue();
}
