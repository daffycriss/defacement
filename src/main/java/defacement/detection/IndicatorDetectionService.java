package defacement.detection;

import org.springframework.stereotype.Service;
import defacement.dto.DetectionResult;
import defacement.model.DefacementIndicator;
import defacement.model.IndicatorType;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IndicatorDetectionService {

    private final Map<IndicatorType, IndicatorDetector> detectors;

    public IndicatorDetectionService(List<IndicatorDetector> detectorList) {

        this.detectors = detectorList.stream()
                .collect(Collectors.toMap(
                        IndicatorDetector::getSupportedType,
                        detector -> detector
                ));
    }

    public DetectionResult detect(
            DefacementIndicator indicator,
            String content) {

        IndicatorDetector detector = detectors.get(indicator.getType());

        if (detector == null) {
            throw new IllegalStateException(
                    "No detector for type " + indicator.getType());
        }

        return switch (indicator.getType()) {
            case STRING -> detector.detect(indicator.getValue(), content);

            case IMAGE_HASH, VIDEO_HASH -> detector.detect(indicator.getHashValue(), content);

            default -> throw new IllegalStateException("Unsupported type");
        };
    }
}