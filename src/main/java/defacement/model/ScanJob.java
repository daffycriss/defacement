package defacement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "scan_jobs")
@Getter
@Setter
public class ScanJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private MonitorTarget target;

    @Column(nullable = false)
    private String status;

    private LocalDateTime startedAt;

    private LocalDateTime finishedAt;

    private Boolean internetAvailable;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    @OneToMany(mappedBy = "scanJob", fetch = FetchType.LAZY)
    private List<ScanResult> scanResults;

    public List<ScanResult> getScanResults() {
        return scanResults;
    }
}
