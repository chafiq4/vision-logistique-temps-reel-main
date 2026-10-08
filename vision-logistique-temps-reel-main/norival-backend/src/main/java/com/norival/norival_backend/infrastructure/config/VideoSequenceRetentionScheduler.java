package com.norival.norival_backend.infrastructure.config;

import com.norival.norival_backend.infrastructure.persistence.entity.AlerteEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataAlerteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class VideoSequenceRetentionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(VideoSequenceRetentionScheduler.class);
    private final SpringDataAlerteRepository alerteRepository;

    public VideoSequenceRetentionScheduler(SpringDataAlerteRepository alerteRepository) {
        this.alerteRepository = alerteRepository;
    }

    /**
     * RG-05: Clean up video sequences/alerts older than 90 days,
     * except HSE alerts in progress (status active).
     * Runs daily at 2:00 AM.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanOldVideoSequences() {
        logger.info("RG-05: Démarrage du nettoyage des anciennes séquences vidéo (seuil 90 jours)...");
        LocalDateTime threshold = LocalDateTime.now().minusDays(90);
        List<AlerteEntity> allAlerts = alerteRepository.findAll();

        List<AlerteEntity> toDelete = allAlerts.stream()
                .filter(a -> a.getTimestamp().isBefore(threshold))
                .filter(a -> !(a.getType().equalsIgnoreCase("HSE") && a.getStatut().equalsIgnoreCase("ACTIVE")))
                .toList();

        if (!toDelete.isEmpty()) {
            alerteRepository.deleteAll(toDelete);
            logger.info("RG-05: Nettoyage terminé. {} alertes/séquences supprimées.", toDelete.size());
        } else {
            logger.info("RG-05: Aucune alerte/séquence à supprimer.");
        }
    }
}
