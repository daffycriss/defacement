package defacement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import defacement.dto.TargetDashboardView;
import defacement.model.MonitorTarget;
import defacement.service.DashboardServiceImpl;
import defacement.service.MonitorTargetServiceImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("/dashboard") // base path
public class DashboardController {

    private final DashboardServiceImpl dashboardService;
    private final MonitorTargetServiceImpl targetService;

    // All users (ADMIN, USER) can see dashboard
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @GetMapping("") // or "/index"
    public String dashboard(Model model) {
        model.addAttribute("summary", dashboardService.getDashboardSummary());
        model.addAttribute("targets", dashboardService.getAllTargetStatuses());
        model.addAttribute("defacedIndicators", dashboardService.getDefacedIndicators());
        return "dashboard";
    }

    // Admin-only target management
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/targets") // unique path
    public String listTargets(Model model) {
        model.addAttribute("targets", targetService.getAllActiveTargets());
        return "targets-list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/targets/new")
    public String showCreateForm(Model model) {
        model.addAttribute("target", new MonitorTarget());
        return "targets-form";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/targets/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        MonitorTarget target = targetService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid target Id:" + id));
        model.addAttribute("target", target);
        return "targets-form";
    }

    // Separate page for defaced indicators (clickable panel)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/defaced-indicators")
    public String showDefacedIndicators(Model model) {
        List<String> defacedIndicators = dashboardService.getDefacedIndicatorsList();
        model.addAttribute("defacedIndicators", dashboardService.getDefacedIndicatorsList());
        return "defaced-indicators"; // Thymeleaf template for full list
    }

    @GetMapping("/api/defaced-count")
    @ResponseBody
    public Map<String, Object> getDefacedIndicatorsCount() {

        List<String> defaced = dashboardService.getDefacedIndicatorsList();

        Map<String, Object> response = new HashMap<>();
        response.put("count", defaced.size());
        response.put("indicators", defaced);

        return response;
    }

    @GetMapping("/api/failed-indicators-count")
    @ResponseBody
    public Map<Long, Integer> getFailedIndicatorsCount() {
        // key = targetId, value = number of failed indicators
        return dashboardService.getAllTargetStatuses().stream()
                .collect(Collectors.toMap(
                        TargetDashboardView::targetId,
                        t -> t.failedIndicators().size()
                ));
    }

    @GetMapping("/api/target-status")
    @ResponseBody
    public List<Map<String, Object>> getTargetStatus() {
        return dashboardService.getAllTargetStatuses().stream()
                .map(t -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", t.targetId());
                    map.put("status", t.status());
                    map.put("lastScanTime", t.lastScanTime());
                    return map;
                })
                .toList();
    }
}
