package defacement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import defacement.service.LogViewerService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/logs")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','USER')")
public class LogViewerController {

    private final LogViewerService logViewerService;

    // Web page
    @GetMapping("")
    public String logsPage(@RequestParam(defaultValue = "200") int lines,
                           @RequestParam(required = false) String keyword,
                           @RequestParam(required = false) String level,
                           Model model) {

        List<String> logs;

        if (keyword != null && !keyword.isBlank()) {
            logs = logViewerService.searchLogs(keyword, lines);
        } else if (level != null && !level.isBlank()) {
            logs = logViewerService.getLogsByLevel(level, lines);
        } else {
            logs = logViewerService.getRecentLogs(lines);
        }

        model.addAttribute("logs", logs);
        model.addAttribute("lines", lines);
        model.addAttribute("keyword", keyword);
        model.addAttribute("level", level);
        return "admin-logs";
    }

    // REST API endpoints
    @GetMapping("/api/recent")
    @ResponseBody
    public Map<String, Object> getRecentLogs(
            @RequestParam(defaultValue = "200") int lines) {
        return Map.of(
                "lines", lines,
                "logs", logViewerService.getRecentLogs(lines)
        );
    }

    @GetMapping("/api/search")
    @ResponseBody
    public Map<String, Object> searchLogs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "200") int lines) {
        return Map.of(
                "keyword", keyword,
                "logs", logViewerService.searchLogs(keyword, lines)
        );
    }

    @GetMapping("/api/level")
    @ResponseBody
    public Map<String, Object> getLogsByLevel(
            @RequestParam String level,
            @RequestParam(defaultValue = "200") int lines) {
        return Map.of(
                "level", level,
                "logs", logViewerService.getLogsByLevel(level, lines)
        );
    }
}