package defacement.detection.Impl;

import org.springframework.stereotype.Component;
import defacement.detection.IndicatorDetector;
import defacement.dto.DetectionResult;
import defacement.model.IndicatorType;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.HexFormat;

@Component
public class VideoIndicatorDetector implements IndicatorDetector {

    @Override
    public IndicatorType getSupportedType() {
        return IndicatorType.VIDEO_HASH;
    }

    @Override
    public DetectionResult detect(String expectedHash, String filePathOrUrl) {

        if (expectedHash == null || filePathOrUrl == null) {
            return new DetectionResult(false, "Invalid input");
        }

        try {
            String algorithm = resolveAlgorithm(expectedHash);
            MessageDigest md = MessageDigest.getInstance(algorithm);

            try (InputStream is = filePathOrUrl.startsWith("http://") || filePathOrUrl.startsWith("https://")
                    ? new URL(filePathOrUrl).openStream()
                    : Files.newInputStream(Paths.get(filePathOrUrl))) {

                byte[] buffer = new byte[8192];
                int bytesRead;

                while ((bytesRead = is.read(buffer)) != -1) {
                    md.update(buffer, 0, bytesRead);
                }
            }

            String actualHash = HexFormat.of().formatHex(md.digest());
            boolean match = expectedHash.equalsIgnoreCase(actualHash);

            return new DetectionResult(
                    match,
                    match ? "Video OK" : "Video defaced"
            );

        } catch (IllegalArgumentException e) {
            return new DetectionResult(false, "Unsupported hash format (must be MD5 or SHA-256)");
        } catch (Exception e) {
            return new DetectionResult(false, "Error processing video: " + e.getMessage());
        }
    }

    private String resolveAlgorithm(String hash) {
        String normalized = hash.trim();

        if (normalized.length() == 32) {
            return "MD5";
        } else if (normalized.length() == 64) {
            return "SHA-256";
        } else {
            throw new IllegalArgumentException("Unknown hash length");
        }
    }
}