package defacement.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import defacement.model.DefacementIndicator;
import defacement.model.IndicatorType;
import defacement.repository.DefacementIndicatorRepository;
import defacement.repository.ScanResultRepository;
import defacement.repository.TargetIndicatorRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefacementIndicatorService {

    private static final Logger logger = LoggerFactory.getLogger(DefacementIndicatorService.class);

    private final DefacementIndicatorRepository repository;
    private final ScanResultRepository scanResultRepository;
    private final TargetIndicatorRepository targetIndicatorRepository;

    public List<DefacementIndicator> findAll() {
        return repository.findAll();
    }

    public List<DefacementIndicator> findEnabled() {
        return repository.findByEnabledTrue();
    }

    public Optional<DefacementIndicator> getById(Long id) {
        return repository.findById(id);
    }

    public DefacementIndicator save(DefacementIndicator indicator) {

        // If hash-based indicator, copy filename into value
        if (indicator.getType() == IndicatorType.IMAGE_HASH ||
                indicator.getType() == IndicatorType.VIDEO_HASH) {

            if (indicator.getHashValue() == null || indicator.getHashValue().isBlank()) {
                throw new IllegalArgumentException("Hash required");
            }

            // Assuming filename is stored in description or a filename field
            // Replace getFilename() with the actual field name if different
            indicator.setValue(indicator.getFilename());
        }

        if (indicator.getId() != null) {
            DefacementIndicator existing = repository.findById(indicator.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid indicator id"));

            if (indicator.getType() == IndicatorType.STRING &&
                    (indicator.getValue() == null || indicator.getValue().isBlank())) {
                throw new IllegalArgumentException("String value required");
            }

            existing.setValue(indicator.getValue());
            existing.setType(indicator.getType());
            existing.setHashValue(indicator.getHashValue());
            existing.setEnabled(indicator.isEnabled());
            existing.setDescription(indicator.getDescription());

            DefacementIndicator updated = repository.save(existing);
            logger.info("Indicator [{}] of type [{}] updated (enabled: {})",
                    updated.getValue(), updated.getType(), updated.isEnabled());
            return updated;
        }

        DefacementIndicator created = repository.save(indicator);
        logger.info("Indicator [{}] of type [{}] created (enabled: {})",
                created.getValue(), created.getType(), created.isEnabled());
        return created;
    }

    public void delete(Long id) {
        DefacementIndicator indicator = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid indicator id"));

        if (!indicator.getTargetMappings().isEmpty()) {
            logger.warn("Attempted to delete indicator [{}] but it is assigned to targets",
                    indicator.getValue());
            throw new IllegalStateException("Cannot delete indicator assigned to targets");
        }

        repository.delete(indicator);
        logger.info("Indicator [{}] of type [{}] deleted", indicator.getValue(), indicator.getType());
    }

    public boolean hasScanResults(DefacementIndicator indicator) {
        return scanResultRepository.existsByIndicator(indicator);
    }

    @Transactional
    public void deleteById(Long id) {
        repository.findById(id).ifPresent(indicator ->
                logger.info("Indicator [{}] of type [{}] force-deleted by ID",
                        indicator.getValue(), indicator.getType())
        );
        repository.deleteById(id);
    }
}