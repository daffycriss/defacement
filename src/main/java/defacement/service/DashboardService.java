package defacement.service;

import defacement.dto.DashboardSummaryView;
import defacement.dto.TargetDashboardView;
import defacement.view.ActiveIndicatorView;
import jakarta.transaction.Transactional;

import java.util.List;

public interface DashboardService {

    List<TargetDashboardView> getAllTargetStatuses();
    DashboardSummaryView getDashboardSummary();
    List<String> getDefacedIndicatorsList();
}