package defacement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "scan_results")
@Getter
@Setter
public class ScanResult extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scan_job_id", nullable = false)
    private ScanJob scanJob;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "indicator_id", nullable = false)
    private DefacementIndicator indicator;

    @Column(nullable = false)
    private boolean found;

    @Column(columnDefinition = "LONGTEXT")
    private String details;

    private LocalDateTime checkedAt;

    private String status;

    public String getStatus() {
        return status;
    }

    public DefacementIndicator getIndicator() {
        return indicator;
    }
}
