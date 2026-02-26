package defacement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import defacement.model.DefacementIndicator;
import defacement.model.IndicatorType;
import defacement.model.User;
import defacement.service.DefacementIndicatorService;

@Controller
@RequestMapping("/indicators")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class DefacementIndicatorController {

    private final DefacementIndicatorService indicatorService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("indicators", indicatorService.findAll());
        return "indicators-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("indicator", new DefacementIndicator());
        model.addAttribute("types", IndicatorType.values());
        return "indicators-form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("indicator") DefacementIndicator indicator,
                       BindingResult result,
                       @AuthenticationPrincipal User user,
                       Model model) {

        if (indicator.getType() == IndicatorType.STRING &&
                (indicator.getValue() == null || indicator.getValue().isBlank())) {

            result.rejectValue("value", "error.value", "Value is required for STRING");
        }

        if ((indicator.getType() == IndicatorType.IMAGE_HASH ||
                indicator.getType() == IndicatorType.VIDEO_HASH) &&
                (indicator.getHashValue() == null || indicator.getHashValue().isBlank())) {

            result.rejectValue("hashValue", "error.hashValue", "Hash is required");
        }

        if (result.hasErrors()) {
            model.addAttribute("types", IndicatorType.values());
            return "indicators-form";
        }

        if (indicator.getId() == null) {
            indicator.setCreatedBy(user);
        }

        indicatorService.save(indicator);
        return "redirect:/indicators";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        DefacementIndicator indicator = indicatorService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid indicator id"));

        model.addAttribute("indicator", indicator);
        model.addAttribute("types", IndicatorType.values());
        return "indicators-form";
    }

    @GetMapping("/{id}/disable")
    public String disable(@PathVariable Long id) {
        DefacementIndicator indicator = indicatorService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid indicator id"));
        indicator.setEnabled(false);
        indicatorService.save(indicator);
        return "redirect:/indicators";
    }

    @GetMapping("/{id}/enable")
    public String enable(@PathVariable Long id) {
        DefacementIndicator indicator = indicatorService.getById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid indicator id"));
        indicator.setEnabled(true);
        indicatorService.save(indicator);
        return "redirect:/indicators";
    }

    @PostMapping("/{id}/delete")
    public String deleteIndicator(@PathVariable Long id) {
        indicatorService.deleteById(id);
        return "redirect:/indicators";
    }
}