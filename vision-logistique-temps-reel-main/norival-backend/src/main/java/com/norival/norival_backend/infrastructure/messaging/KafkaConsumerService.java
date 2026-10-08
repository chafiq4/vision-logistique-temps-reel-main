package com.norival.norival_backend.infrastructure.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;
    private final com.norival.norival_backend.infrastructure.persistence.repository.SpringDataRotationRepository rotationRepository;
    private final com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository vehiculeRepository;
    private final com.norival.norival_backend.infrastructure.persistence.repository.SpringDataChantierRepository chantierRepository;
    private final com.norival.norival_backend.infrastructure.persistence.repository.SpringDataAlerteRepository alerteRepository;

    // Track the last unconfirmed action for each vehicle at each chantier for anti-bounce (RG-02)
    private final Map<String, String> lastUnconfirmedAction = new ConcurrentHashMap<>();

    public KafkaConsumerService(
            ObjectMapper objectMapper, 
            org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataRotationRepository rotationRepository,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository vehiculeRepository,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataChantierRepository chantierRepository,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataAlerteRepository alerteRepository) {
        this.objectMapper = objectMapper;
        this.messagingTemplate = messagingTemplate;
        this.rotationRepository = rotationRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.chantierRepository = chantierRepository;
        this.alerteRepository = alerteRepository;
    }

    @KafkaListener(topics = "norival-detections", groupId = "norival-group", autoStartup = "${spring.kafka.enabled:false}")
    public void consumeDetection(String message) {
        logger.info("Message Kafka recu: {}", message);
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> event = objectMapper.readValue(message, Map.class);
            String matricule = (String) event.get("matricule");
            
            // Verifier si le vehicule existe dans la base de donnees
            var vehiculeOpt = vehiculeRepository.findByMatricule(matricule);
            if (vehiculeOpt.isEmpty()) {
                logger.warn("Avertissement: Vehicule avec matricule {} non enregistre en BDD. Evenement ignore.", matricule);
                return;
            }
            var vehicule = vehiculeOpt.get();
            
            String type = (String) event.get("type");
            String action = (String) event.get("action");
            Object chantierIdObj = event.get("chantierId");
            Long chantierId = Long.valueOf(chantierIdObj.toString());

            // Verifier si le chantier existe dans la base de donnees
            if (!chantierRepository.existsById(chantierId)) {
                logger.warn("Avertissement: Chantier avec l'ID {} non enregistre en BDD. Evenement ignore.", chantierId);
                return;
            }

            // RG-02 : Anti-rebond (confirmer l'entrée/sortie par 2 passages successifs)
            String key = matricule + "_" + chantierId;
            String previousAction = lastUnconfirmedAction.get(key);
            if (previousAction == null || !previousAction.equals(action)) {
                lastUnconfirmedAction.put(key, action);
                logger.info("RG-02 [Anti-Rebond]: Premier passage pour {} à {}. Action [{}] enregistrée en attente de confirmation.", matricule, chantierId, action);
                return;
            }
            // Confirmé ! Supprimer du cache
            lastUnconfirmedAction.remove(key);
            logger.info("RG-02 [Anti-Rebond]: Deuxième passage successif confirmé pour {} à {}. Action [{}] validée.", matricule, chantierId, action);

            String timestamp = (String) event.get("timestamp");

            logger.info("ALERTE FLUX LOGISTIQUE : Vehicule {} ({}) detecte en [{}] sur le chantier {} a {}",
                    matricule, type, action, chantierId, timestamp);

            // Enregistrer l'événement de rotation en base de données
            com.norival.norival_backend.infrastructure.persistence.entity.RotationEntity rotation = 
                new com.norival.norival_backend.infrastructure.persistence.entity.RotationEntity(
                    matricule, type, chantierId, action, LocalDateTime.now()
                );
            rotationRepository.save(rotation);
            logger.info("Evenement de rotation enregistre en BDD.");

            // Diffuser aux WebSockets
            messagingTemplate.convertAndSend("/topic/detections", event);

            // RG-03 : Alerte critique en cas d'accès non autorisé
            if (vehicule.getChantierId() == null || !vehicule.getChantierId().equals(chantierId)) {
                String errorMsg = String.format("Accès non autorisé: Le véhicule %s (%s) a été détecté en [%s] sur le chantier %d alors qu'il n'y est pas affecté.",
                        matricule, type, action, chantierId);
                logger.warn("RG-03 [ALERTE CRITIQUE]: {}", errorMsg);

                com.norival.norival_backend.infrastructure.persistence.entity.AlerteEntity alerte =
                    new com.norival.norival_backend.infrastructure.persistence.entity.AlerteEntity(
                        null, "LOGISTIQUE", errorMsg, "ACTIVE", LocalDateTime.now(), chantierId, matricule, "CRITIQUE"
                    );
                alerteRepository.save(alerte);

                // Diffuser l'alerte temps réel via WebSocket
                messagingTemplate.convertAndSend("/topic/alerts", alerte);
            }

        } catch (Exception e) {
            logger.error("Erreur de parsing ou d'enregistrement de l'evenement Kafka: {}", e.getMessage(), e);
        }
    }
}
