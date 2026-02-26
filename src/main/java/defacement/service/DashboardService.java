package defacement.service;

import defacement.dto.DashboardSummaryView;
import defacement.dto.TargetDashboardView;
import java.util.List;

public interface DashboardService {

    List<TargetDashboardView> getAllTargetStatuses();
    DashboardSummaryView getDashboardSummary();
    List<String> getDefacedIndicatorsList();
}