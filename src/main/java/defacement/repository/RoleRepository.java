package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import defacement.model.Role;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.Optional;

@EnableJpaRepositories
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
