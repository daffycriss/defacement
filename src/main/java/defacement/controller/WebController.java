package defacement.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebController {

    @GetMapping("/login")
    public String login() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is already logged in
        if (auth != null && auth.isAuthenticated() &&
                !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            return "redirect:/default"; // or "/home"
        }

        return "login";
    }

    @GetMapping("/default")
    public String defaultAfterLogin(Authentication authentication) {
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/admin";
        }
        return "redirect:/user";
    }

    @GetMapping("/user")
    public String userPage() {
        return "user"; // Thymeleaf template for normal users
    }

    @GetMapping("/admin")
    public String adminPage() {
        return "admin"; // Thymeleaf template for admins
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}
