package defacement.detection;

import defacement.dto.DetectionResult;
import defacement.model.IndicatorType;

public interface IndicatorDetector {
    IndicatorType getSupportedType();
    DetectionResult detect(String value, String content);
}
