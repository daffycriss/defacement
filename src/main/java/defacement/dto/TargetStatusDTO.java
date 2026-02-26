package defacement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class TargetStatusDTO {
    private Long targetId;
    private String name;
    private String baseUrl;
    private String status;
    private LocalDateTime lastScanAt;
    private int activeAlerts;

    public String getStatusClass() {
        return switch (status) {
            case "OK" -> "text-success";
            case "DEFACED" -> "text-danger";
            case "ERROR" -> "text-warning";
            default -> "text-secondary";
        };
    }
}