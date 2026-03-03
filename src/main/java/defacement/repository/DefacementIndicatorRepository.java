package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import defacement.model.DefacementIndicator;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

@EnableJpaRepositories
public interface DefacementIndicatorRepository
        extends JpaRepository<DefacementIndicator, Long> {

    List<DefacementIndicator> findByEnabledTrue();
}
