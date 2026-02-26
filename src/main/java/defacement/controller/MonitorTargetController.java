package defacement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import defacement.model.DefacementIndicator;
import defacement.model.MonitorTarget;
import defacement.model.TargetIndicator;
import defacement.repository.DefacementIndicatorRepository;
import defacement.repository.MonitorTargetRepository;
import defacement.repository.TargetIndicatorRepository;
import defacement.service.DefacementIndicatorService;
import defacement.service.MonitorTargetServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/targets")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // All endpoints restricted to ADMIN
public class MonitorTargetController {

    private final MonitorTargetServiceImpl targetService;
    private final DefacementIndicatorService indicatorService;
    private final MonitorTargetRepository targetRepository;
    private final DefacementIndicatorRepository indicatorRepository;
    private final TargetIndicatorRepository targetIndicatorRepository;

    // List all active targets
    @GetMapping
    public String listTargets(Model model) {
        model.addAttribute("targets", targetService.getAllActiveTargets());
        return "targets-list";
    }

    // Show form for creating a new target
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("target", new MonitorTarget());
        model.addAttribute("allIndicators", indicatorService.findEnabled());
        return "targets-form";
    }

    // Show form for editing an existing target
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        MonitorTarget target = targetService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid target Id:" + id));
        model.addAttribute("target", target);
        model.addAttribute("allIndicators", indicatorService.findEnabled());
        return "targets-form";
    }

    // Save a new or edited target
    @PostMapping("/save")
    public String saveTarget(@Valid @ModelAttribute("target") MonitorTarget target,
                             BindingResult result,
                             Model model) {

        if (result.hasErrors()) {
            model.addAttribute("allIndicators", indicatorService.findEnabled());
            return "targets-form";
        }

        // Convert selected indicator IDs (bound automatically) to entities
        if (target.getIndicators() != null) {
            Set<DefacementIndicator> actualIndicators = new HashSet<>();
            for (DefacementIndicator i : target.getIndicators()) {
                indicatorService.getById(i.getId()).ifPresent(actualIndicators::add);
            }
            target.setIndicators(actualIndicators);
        }

        targetService.save(target);
        return "redirect:/targets";
    }

    // Soft-delete a target
    @GetMapping("/{id}/delete")
    public String deleteTarget(@PathVariable Long id) {
        targetService.softDelete(id);
        return "redirect:/targets";
    }

    // Show indicators for a target
    @GetMapping("/{id}/indicators")
    public String showTargetIndicators(@PathVariable Long id, Model model) {
        MonitorTarget target = targetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Target not found: " + id));

        List<TargetIndicator> allMappings = targetIndicatorRepository.findByTarget(target);

        // Keep only enabled indicators
        List<TargetIndicator> enabledMappings = allMappings.stream()
                .filter(mapping -> mapping.getIndicator().isEnabled())
                .toList();

        model.addAttribute("target", target);
        model.addAttribute("indicators", enabledMappings);

        return "target-indicators";
    }

    // Show form to assign a new indicator to a target
    @GetMapping("/{id}/indicators/new")
    public String showAddIndicatorForm(@PathVariable Long id, Model model) {
        MonitorTarget target = targetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Target not found: " + id));

        // Fetch all enabled indicators
        List<DefacementIndicator> enabledIndicators = indicatorRepository.findByEnabledTrue();

        // Fetch all indicator IDs already assigned to any target
        List<Long> assignedIds = targetIndicatorRepository.findAll()
                .stream()
                .map(mapping -> mapping.getIndicator().getId())
                .toList();

        // Only show indicators not assigned to any target
        List<DefacementIndicator> availableIndicators = enabledIndicators.stream()
                .filter(indicator -> !assignedIds.contains(indicator.getId()))
                .toList();

        model.addAttribute("target", target);
        model.addAttribute("availableIndicators", availableIndicators);
        model.addAttribute("indicator", new TargetIndicator()); // for form binding

        return "add-target-indicator";
    }


    // Handle POST to assign indicator
    @PostMapping("/{id}/indicators")
    public String addIndicatorToTarget(
            @PathVariable Long id,
            @RequestParam("indicatorId") Long indicatorId,
            RedirectAttributes redirectAttributes
    ) {
        MonitorTarget target = targetRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Target not found: " + id));

        DefacementIndicator indicator = indicatorRepository.findById(indicatorId)
                .orElseThrow(() -> new IllegalArgumentException("Indicator not found"));

        // Check if this indicator is already assigned to any target
        if (targetIndicatorRepository.existsByIndicator(indicator)) {
            redirectAttributes.addFlashAttribute("error", "This indicator is already assigned to a target.");
            return "redirect:/targets/" + id + "/indicators";
        }

        // Double-check: prevent assigning the same indicator twice to the same target
        if (targetIndicatorRepository.existsByTargetAndIndicator(target, indicator)) {
            redirectAttributes.addFlashAttribute("error", "This indicator is already assigned to this target.");
            return "redirect:/targets/" + id + "/indicators";
        }

        // Safe to assign
        TargetIndicator mapping = new TargetIndicator();
        mapping.setTarget(target);
        mapping.setIndicator(indicator);
        targetIndicatorRepository.save(mapping);

        redirectAttributes.addFlashAttribute("success", "Indicator assigned successfully.");
        return "redirect:/targets/" + id + "/indicators";
    }

    @GetMapping("/{id}/indicators/{mappingId}/remove")
    public String removeIndicatorFromTarget(@PathVariable Long id,
                                            @PathVariable Long mappingId,
                                            RedirectAttributes redirectAttributes) {
        TargetIndicator mapping = targetIndicatorRepository.findById(mappingId)
                .orElseThrow(() -> new IllegalArgumentException("Mapping not found: " + mappingId));

        targetIndicatorRepository.delete(mapping);

        redirectAttributes.addFlashAttribute("success", "Indicator removed from target successfully!");
        return "redirect:/targets/" + id + "/indicators";
    }
}