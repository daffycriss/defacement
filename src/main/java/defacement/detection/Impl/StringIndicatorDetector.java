package defacement.detection.Impl;

import org.springframework.stereotype.Component;
import defacement.detection.IndicatorDetector;
import defacement.dto.DetectionResult;
import defacement.model.IndicatorType;

@Component
public class StringIndicatorDetector implements IndicatorDetector {

    @Override
    public IndicatorType getSupportedType() {
        return IndicatorType.STRING;
    }

    @Override
    public DetectionResult detect(String value, String content) {

        if (value == null || content == null) {
            return new DetectionResult(false, "Invalid input");
        }

        boolean found = content.contains(value);

        return new DetectionResult(
                found,
                found ? "String found" : "String not found"
        );
    }
}

