package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import defacement.model.ConnectivityCheck;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

public interface ConnectivityCheckRepository
        extends JpaRepository<ConnectivityCheck, Long> {
}
