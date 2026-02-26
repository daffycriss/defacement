package defacement.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConnectivityCheckResult {
    private boolean available;
    private Integer latencyMs;
    private String errorMessage;
}
