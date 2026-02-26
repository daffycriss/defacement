package defacement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import defacement.model.ConnectivityCheck;

public interface ConnectivityCheckRepository
        extends JpaRepository<ConnectivityCheck, Long> {
}
