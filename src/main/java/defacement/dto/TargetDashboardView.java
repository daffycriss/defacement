package defacement.dto;

import java.time.LocalDateTime;
import java.util.List;

public record TargetDashboardView(
        Long targetId,
        String targetName,
        String baseUrl,
        String status,
        LocalDateTime lastScanTime,
        int failedIndicatorsCount,
        List<String> failedIndicators
) {}
