package defacement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "defacement_indicators")
@Getter
@Setter
public class DefacementIndicator extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IndicatorType type;

    @Column(columnDefinition = "TEXT")
    private String value;

    @Column(length = 128)
    private String hashValue;

    @Column
    private String filename;

    private String description;

    @Column(nullable = false)
    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;

    @OneToMany(mappedBy = "indicator",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<TargetIndicator> targetMappings = new ArrayList<>();

    public String getValue() {
        return value;
    }

    public String getHashValue() {
        return hashValue;
    }
}
