package defacement.detection.Impl;

import org.springframework.stereotype.Component;
import defacement.detection.IndicatorDetector;
import defacement.dto.DetectionResult;
import defacement.model.IndicatorType;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class ImageIndicatorDetector implements IndicatorDetector {

    @Override
    public IndicatorType getSupportedType() {
        return IndicatorType.IMAGE_HASH;
    }

    @Override
    public DetectionResult detect(String expectedHash, String filePathOrUrl) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filePathOrUrl));
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(bytes);
            String actualHash = HexFormat.of().formatHex(hashBytes);

            boolean found = expectedHash.equalsIgnoreCase(actualHash);
            return new DetectionResult(found, found ? "Image OK" : "Image defaced");

        } catch (Exception e) {
            return new DetectionResult(false, "Error reading image: " + e.getMessage());
        }
    }
}

