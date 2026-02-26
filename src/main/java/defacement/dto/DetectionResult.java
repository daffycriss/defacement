package defacement.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DetectionResult {
    private boolean found;
    private String details;
}
