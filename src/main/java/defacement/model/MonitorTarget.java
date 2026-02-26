package defacement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "monitor_targets")
@Getter
@Setter
public class MonitorTarget extends BaseEntity {

    @Column(nullable = false)
    @NotBlank(message = "Name is required")
    private String name;

    @Column(nullable = false, length = 2048)
    @NotBlank(message = "Base URL is required")
    @URL(message = "Must be a valid URL")
    private String baseUrl;

    @Column(nullable = false)
    private boolean scanEnabled = true;

    @Column(nullable = false)
    private Integer scanIntervalMin = 10;

    private LocalDateTime lastScanAt;

    @ManyToMany
    @JoinTable(
            name = "target_indicators",
            joinColumns = @JoinColumn(name = "target_id"),
            inverseJoinColumns = @JoinColumn(name = "indicator_id")
    )
    private Set<DefacementIndicator> indicators = new HashSet<>();
}
