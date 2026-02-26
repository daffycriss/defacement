package defacement.fetcher;

import java.time.Duration;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ContentFetcher {

    private final RestTemplate restTemplate;

    public ContentFetcher() {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(10).toMillis());

        this.restTemplate = new RestTemplate(factory);
    }

    public String fetch(String url) {
        return restTemplate.getForObject(url, String.class);
    }
}
