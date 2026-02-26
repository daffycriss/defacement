package defacement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "target_indicators")
public class TargetIndicator extends BaseEntity {

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "indicator_id")
    private DefacementIndicator indicator;

    @ManyToOne
    @JoinColumn(name = "target_id")
    private MonitorTarget target;

}
