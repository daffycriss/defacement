package defacement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "connectivity_checks")
@Getter
@Setter
public class ConnectivityCheck extends BaseEntity {

    @Column(nullable = false)
    private boolean internetAvailable;

    private Integer latencyMs;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private LocalDateTime checkedAt;
}
