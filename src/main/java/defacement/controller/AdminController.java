package defacement.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import defacement.model.Sex;
import defacement.model.User;
import defacement.repository.RoleRepository;
import defacement.service.AdminUserService;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminUserService adminUserService;
    private final RoleRepository roleRepository; // only for populating form dropdowns

    @GetMapping("/users")
    public String listUsers(Model model) {
        model.addAttribute("users", adminUserService.findAllActiveUsers());
        return "admin-users";
    }

    @GetMapping("/users/deleted")
    public String listDeletedUsers(Model model) {
        model.addAttribute("users", adminUserService.findAllDeletedUsers());
        return "admin-users-deleted";
    }

    @GetMapping("/users/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("sexes", Sex.values());
        return "admin-user-create";
    }

    @PostMapping("/users/new")
    public String createUser(@Valid @ModelAttribute User user,
                             @RequestParam Long roleId,
                             @RequestParam String password,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        try {
            adminUserService.createUser(user, roleId, password);
            redirectAttributes.addFlashAttribute("success", "User created successfully");
            return "redirect:/admin/users";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("sexes", Sex.values());
            return "admin-user-create";
        }
    }

    @PostMapping("/users/{id}/delete")
    public String softDeleteUser(@PathVariable Long id,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {
        try {
            adminUserService.softDeleteUser(id, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "User deleted successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/users/{id}/edit")
    public String showEditUser(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            User user = adminUserService.findUserById(id);
            model.addAttribute("user", user);
            model.addAttribute("roles", roleRepository.findAll());
            return "admin-user-edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users/{id}/edit")
    public String updateUser(@PathVariable Long id,
                             @ModelAttribute User formUser,
                             @RequestParam(required = false) Long roleId,
                             RedirectAttributes redirectAttributes) {
        try {
            adminUserService.updateUser(id, formUser, roleId);
            redirectAttributes.addFlashAttribute("success", "User updated successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/restore")
    public String restoreUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminUserService.restoreUser(id);
            redirectAttributes.addFlashAttribute("success", "User restored successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users/deleted";
    }

    @GetMapping("/users/{id}/password")
    public String showChangePasswordForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("user", adminUserService.findUserById(id));
            return "admin-user-change-password";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/users/{id}/change-password")
    public String changePassword(@PathVariable Long id,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match");
            return "redirect:/admin/users/" + id + "/password";
        }
        try {
            adminUserService.changePassword(id, newPassword);
            redirectAttributes.addFlashAttribute("success", "Password changed successfully");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users/" + id + "/password";
        }
        return "redirect:/admin/users";
    }
}