package defacement.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import defacement.model.Role;
import defacement.model.Sex;
import defacement.model.User;
import defacement.repository.RoleRepository;
import defacement.repository.UserRepository;

import java.time.LocalDate;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role("ADMIN")));
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> roleRepository.save(new Role("USER")));

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User("admin", passwordEncoder.encode("admin123"));
                admin.setSystemUser(true);
                admin.addRole(adminRole);
                admin.setFirstName("Admin");
                admin.setLastName("Admin");
                admin.setDateOfBirth(LocalDate.of(1990,1,1));
                admin.setSex(Sex.OTHER);
                admin.setIdNumber("AD000001");
                userRepository.save(admin);
            }

            if (userRepository.findByUsername("user").isEmpty()) {
                User user = new User("user", passwordEncoder.encode("user123"));
                user.addRole(userRole);
                user.setFirstName("User");
                user.setLastName("User");
                user.setDateOfBirth(LocalDate.of(1990,1,1));
                user.setSex(Sex.OTHER);
                user.setIdNumber("US000001");
                userRepository.save(user);
            }
        };
    }
}
