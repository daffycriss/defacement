package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import defacement.model.DefacementIndicator;
import defacement.model.ScanJob;
import defacement.model.ScanResult;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;

public interface ScanResultRepository
        extends JpaRepository<ScanResult, Long> {

    boolean existsByIndicator(DefacementIndicator indicator);

    long countByScanJobAndFoundFalse(ScanJob scanJob);

    // Fetch all ScanResults that are DEFACED
    @Query("SELECT r FROM ScanResult r WHERE r.status = 'DEFACED'")
    List<ScanResult> findAllDefacedScanResults();

    List<ScanResult> findAllByStatus(String status);

    List<ScanResult> findByScanJob(ScanJob scanJob);
}
