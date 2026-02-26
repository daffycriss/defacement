package defacement.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import defacement.model.MonitorTarget;
import defacement.repository.MonitorTargetRepository;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MonitorTargetServiceImpl implements MonitorTargetService{

    private final MonitorTargetRepository targetRepository;
    private static final Logger logger = LoggerFactory.getLogger(MonitorTargetServiceImpl.class);

    public List<MonitorTarget> getAllActiveTargets() {
        return targetRepository.findByDeletedAtIsNull();
    }

    public Optional<MonitorTarget> getById(Long id) {
        return targetRepository.findById(id)
                .filter(t -> t.getDeletedAt() == null);
    }

    @Transactional
    public MonitorTarget save(MonitorTarget target) {
        boolean isNew = target.getId() == null;
        MonitorTarget saved = targetRepository.save(target);
        if (isNew)
            logger.info("Target created: [{}] URL [{}]", saved.getName(), saved.getBaseUrl());
        else
            logger.info("Target updated: [{}]", saved.getName());
        return saved;
    }

    @Transactional
    public void softDelete(Long id) {
        targetRepository.findById(id).ifPresent(target -> {
            target.setDeletedAt(LocalDateTime.now());
            targetRepository.save(target);
            logger.info("Target soft-deleted: [{}]", target.getName());
        });
    }

    public List<MonitorTarget> getAllTargets() {
        return targetRepository.findAll();
    }
}