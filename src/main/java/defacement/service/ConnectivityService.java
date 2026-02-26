package defacement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import defacement.dto.ConnectivityCheckResult;
import defacement.model.ConnectivityCheck;
import defacement.repository.ConnectivityCheckRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConnectivityService {

    private final ConnectivityCheckRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();

    public ConnectivityCheckResult checkInternet() {

        ConnectivityCheckResult result = new ConnectivityCheckResult();
        long start = System.currentTimeMillis();

        try {
            restTemplate.getForEntity("https://www.google.com", String.class);
            result.setAvailable(true);
            result.setLatencyMs((int) (System.currentTimeMillis() - start));
        } catch (Exception ex) {
            result.setAvailable(false);
            result.setErrorMessage(ex.getMessage());
        }

        saveResult(result);
        return result;
    }

    private void saveResult(ConnectivityCheckResult result) {
        ConnectivityCheck entity = new ConnectivityCheck();
        entity.setInternetAvailable(result.isAvailable());
        entity.setLatencyMs(result.getLatencyMs());
        entity.setErrorMessage(result.getErrorMessage());
        entity.setCheckedAt(LocalDateTime.now());
        repository.save(entity);
    }
}
