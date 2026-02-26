package defacement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 3, max = 50)
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = true)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(nullable = true)
    private String lastName;

    @Past
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false)
    @NotNull(message = "Sex is required")
    @Enumerated(EnumType.STRING)
    private Sex sex;

    @NotBlank
    @Size(max = 30)
    @Column(unique = true, nullable = true)
    private String idNumber;

    private boolean enabled = true;

    @Column(nullable = false)
    private boolean systemUser = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.enabled = true;
    }

    // REQUIRED
    public void addRole(Role role) {
        this.roles.add(role);
    }

    // Optional helper
    public boolean hasRole(String roleName) {
        return roles.stream()
                .anyMatch(r -> r.getName().equals(roleName));
    }

    // Setter for password
    public void setPassword(String password) { this.password = password; }

    // Setter for username
    public void setUsername(String username) {
        this.username = username;
    }

    // Setter for firstname
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Setter for lastname
    public void setLastName(String lastName) {this.lastName = lastName; }

    // Setter for dateOfBirth
    public void setDateOfBirth(LocalDate dateOfBirth) {this.dateOfBirth = dateOfBirth; }

    // Setter for Sex
    public void setSex(Sex sex) {this.sex = sex; }

    // Setter for idNumber
    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    // Password change
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public boolean isSystemUser() { return systemUser; }

    public void setSystemUser(boolean systemUser) { this.systemUser = systemUser; }

    public void softDelete(String adminUsername) {
        this.enabled = false;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = adminUsername;
    }

    public void restore() {
        this.enabled = true;
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
