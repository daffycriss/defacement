package defacement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import defacement.service.PasswordValidator;
import defacement.service.UserService;

@Controller
@RequiredArgsConstructor
public class PasswordController {

    private final UserService userService;

    @GetMapping("/change-password")
    public String showChangePasswordForm() {
        return "change-password"; // Thymeleaf template
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestParam String oldPassword,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmNewPassword,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (!newPassword.equals(confirmNewPassword)) {
            model.addAttribute("error", "New password and confirm password do not match");
            return "change-password";
        }

        if (!PasswordValidator.isValid(newPassword)) {
            redirectAttributes.addFlashAttribute("error",
                    "Password must be at least 8 characters, include uppercase, lowercase, number, and special character");
            return "/change-password";
        }

        try {
            userService.changePassword(oldPassword, newPassword);
            model.addAttribute("success", "Password changed successfully");
            return "change-password";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "change-password";
        }
    }
}