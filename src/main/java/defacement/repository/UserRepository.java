package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import defacement.model.User;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    // ✅ Active users only
    List<User> findAllByEnabledTrue();

    // (Optional, for later)
    List<User> findAllByEnabledFalse();
}
