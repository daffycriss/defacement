package defacement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardSummaryView {

    private long totalTargets;
    private long healthyTargets;
    private long defacedTargets;
    private long failedTargets;
    private long activeAlerts;
}
