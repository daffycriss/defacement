package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import defacement.model.DefacementIndicator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

//public interface DefacementIndicatorRepository
//        extends JpaRepository<DefacementIndicator, Long> {
//
//    List<DefacementIndicator> findByEnabledTrue();
//}

public interface DefacementIndicatorRepository
        extends JpaRepository<DefacementIndicator, Long> {

    List<DefacementIndicator> findByEnabledTrue();

    @Query("""
        SELECT i
        FROM DefacementIndicator i
        LEFT JOIN FETCH i.createdBy
        ORDER BY i.id
    """)
    List<DefacementIndicator> findAllWithCreatedBy();
}
