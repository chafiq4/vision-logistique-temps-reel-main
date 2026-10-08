package com.norival.norival_backend.infrastructure.rest;

import com.norival.norival_backend.application.usecase.VehiculeUseCase;
import com.norival.norival_backend.domain.model.Vehicule;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicules")
@CrossOrigin(origins = "*")
public class VehiculeController {

    private final VehiculeUseCase vehiculeUseCase;
    private final com.norival.norival_backend.infrastructure.persistence.repository.SpringDataRotationRepository rotationRepository;
    private final org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;
    private final com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository vehiculeRepository;
    private final com.norival.norival_backend.infrastructure.config.ConfigAuditHelper auditHelper;
    private final com.norival.norival_backend.application.service.GeofenceService geofenceService;
    private final com.norival.norival_backend.infrastructure.persistence.repository.SpringDataAlerteRepository alerteRepository;

    public VehiculeController(
            VehiculeUseCase vehiculeUseCase,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataRotationRepository rotationRepository,
            org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository vehiculeRepository,
            com.norival.norival_backend.infrastructure.config.ConfigAuditHelper auditHelper,
            com.norival.norival_backend.application.service.GeofenceService geofenceService,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataAlerteRepository alerteRepository) {
        this.vehiculeUseCase = vehiculeUseCase;
        this.rotationRepository = rotationRepository;
        this.messagingTemplate = messagingTemplate;
        this.vehiculeRepository = vehiculeRepository;
        this.auditHelper = auditHelper;
        this.geofenceService = geofenceService;
        this.alerteRepository = alerteRepository;
    }

    @GetMapping
    public List<Vehicule> getVehicules() {
        return vehiculeUseCase.obtenirTousLesVehicules();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicule> getVehiculeById(@PathVariable Long id) {
        return vehiculeUseCase.obtenirVehiculeParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createVehicule(@RequestBody Vehicule vehicule) {
        if (vehicule.getConducteurId() != null) {
            if (vehiculeRepository.existsByConducteurId(vehicule.getConducteurId())) {
                return ResponseEntity.badRequest().body("RG-08: Un conducteur ne peut être associé qu'à un seul engin actif à la fois.");
            }
        }
        Vehicule saved = vehiculeUseCase.enregistrerVehicule(vehicule);
        auditHelper.log("CREATE", "VEHICULE", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateVehicule(@PathVariable Long id, @RequestBody Vehicule vehicule) {
        if (vehicule.getConducteurId() != null) {
            if (vehiculeRepository.existsByConducteurIdAndIdNot(vehicule.getConducteurId(), id)) {
                return ResponseEntity.badRequest().body("RG-08: Un conducteur ne peut être associé qu'à un seul engin actif à la fois.");
            }
        }
        return vehiculeUseCase.obtenirVehiculeParId(id)
                .map(existing -> {
                    boolean becamePanne = !existing.getStatut().equals("EN_PANNE") && vehicule.getStatut().equals("EN_PANNE");
                    vehicule.setId(id);
                    Vehicule saved = vehiculeUseCase.enregistrerVehicule(vehicule);
                    auditHelper.log("UPDATE", "VEHICULE", saved.getId());

                    if (becamePanne) {
                        com.norival.norival_backend.infrastructure.persistence.entity.AlerteEntity alerte =
                            new com.norival.norival_backend.infrastructure.persistence.entity.AlerteEntity();
                        alerte.setType("PANNE_MECANIQUE");
                        alerte.setCriticite("CRITIQUE");
                        alerte.setMessage("Véhicule " + saved.getMatricule() + " signalé en panne par un administrateur.");
                        alerte.setStatut("ACTIVE");
                        alerte.setTimestamp(java.time.LocalDateTime.now());
                        
                        alerte = alerteRepository.save(alerte);
                        messagingTemplate.convertAndSend("/topic/alerts", alerte);
                    }

                    // Diffuser le changement de statut pour la carte
                    java.util.Map<String, Object> event = new java.util.HashMap<>();
                    event.put("matricule", saved.getMatricule());
                    event.put("type", saved.getType());
                    event.put("statut", saved.getStatut());
                    event.put("action", "STATUS_UPDATE");
                    if (saved.getLatitude() != null) {
                        event.put("latitude", saved.getLatitude());
                        event.put("longitude", saved.getLongitude());
                    }
                    event.put("timestamp", java.time.LocalDateTime.now().toString());
                    messagingTemplate.convertAndSend("/topic/detections", event);

                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/depart")
    public ResponseEntity<Vehicule> depart(@PathVariable Long id, @RequestParam Long chantierId) {
        return vehiculeUseCase.obtenirVehiculeParId(id)
                .map(v -> {
                    v.setStatut("EN_TRANSIT");
                    Vehicule saved = vehiculeUseCase.enregistrerVehicule(v);

                    // Enregistrer l'événement de rotation SORTIE (Départ)
                    com.norival.norival_backend.infrastructure.persistence.entity.RotationEntity rotation = 
                        new com.norival.norival_backend.infrastructure.persistence.entity.RotationEntity(
                            v.getMatricule(), v.getType(), chantierId, "SORTIE", java.time.LocalDateTime.now()
                        );
                    rotationRepository.save(rotation);

                    // Diffuser aux WebSockets
                    java.util.Map<String, Object> event = new java.util.HashMap<>();
                    event.put("matricule", v.getMatricule());
                    event.put("type", v.getType());
                    event.put("action", "SORTIE");
                    event.put("chantierId", chantierId);
                    event.put("timestamp", java.time.LocalDateTime.now().toString());
                    messagingTemplate.convertAndSend("/topic/detections", event);

                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/arrivee")
    public ResponseEntity<Vehicule> arrivee(@PathVariable Long id, @RequestParam Long chantierId) {
        return vehiculeUseCase.obtenirVehiculeParId(id)
                .map(v -> {
                    v.setStatut("DISPONIBLE");
                    Vehicule saved = vehiculeUseCase.enregistrerVehicule(v);

                    // Enregistrer l'événement de rotation ENTREE (Arrivée)
                    com.norival.norival_backend.infrastructure.persistence.entity.RotationEntity rotation = 
                        new com.norival.norival_backend.infrastructure.persistence.entity.RotationEntity(
                            v.getMatricule(), v.getType(), chantierId, "ENTREE", java.time.LocalDateTime.now()
                        );
                    rotationRepository.save(rotation);

                    // Diffuser aux WebSockets
                    java.util.Map<String, Object> event = new java.util.HashMap<>();
                    event.put("matricule", v.getMatricule());
                    event.put("type", v.getType());
                    event.put("action", "ENTREE");
                    event.put("chantierId", chantierId);
                    event.put("timestamp", java.time.LocalDateTime.now().toString());
                    messagingTemplate.convertAndSend("/topic/detections", event);

                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/panne")
    public ResponseEntity<Vehicule> declarerPanne(@PathVariable Long id) {
        return vehiculeUseCase.obtenirVehiculeParId(id)
                .map(v -> {
                    v.setStatut("EN_PANNE");
                    Vehicule saved = vehiculeUseCase.enregistrerVehicule(v);

                    // Créer et enregistrer une alerte
                    com.norival.norival_backend.infrastructure.persistence.entity.AlerteEntity alerte =
                        new com.norival.norival_backend.infrastructure.persistence.entity.AlerteEntity();
                    alerte.setType("PANNE_MECANIQUE");
                    alerte.setCriticite("CRITIQUE");
                    alerte.setMessage("Véhicule " + v.getMatricule() + " en panne à la position " + v.getLatitude() + ", " + v.getLongitude());
                    alerte.setStatut("ACTIVE");
                    alerte.setTimestamp(java.time.LocalDateTime.now());
                    
                    alerte = alerteRepository.save(alerte);

                    // Diffuser l'alerte aux WebSockets
                    messagingTemplate.convertAndSend("/topic/alerts", alerte);

                    // Diffuser aussi une détection spécifique
                    java.util.Map<String, Object> event = new java.util.HashMap<>();
                    event.put("matricule", v.getMatricule());
                    event.put("type", v.getType());
                    event.put("action", "PANNE");
                    event.put("latitude", v.getLatitude());
                    event.put("longitude", v.getLongitude());
                    event.put("timestamp", java.time.LocalDateTime.now().toString());
                    messagingTemplate.convertAndSend("/topic/detections", event);

                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicule(@PathVariable Long id) {
        vehiculeUseCase.supprimerVehicule(id);
        auditHelper.log("DELETE", "VEHICULE", id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/position")
    public ResponseEntity<Vehicule> updatePosition(@PathVariable Long id, @RequestBody Map<String, Double> payload) {
        return vehiculeUseCase.obtenirVehiculeParId(id)
                .map(v -> {
                    if (payload.containsKey("latitude") && payload.containsKey("longitude")) {
                        v.setLatitude(payload.get("latitude"));
                        v.setLongitude(payload.get("longitude"));
                        Vehicule saved = vehiculeUseCase.enregistrerVehicule(v);
                        
                        // Check Geofence
                        geofenceService.checkPosition(saved);
                        
                        // Broadcast location update
                        java.util.Map<String, Object> event = new java.util.HashMap<>();
                        event.put("matricule", v.getMatricule());
                        event.put("latitude", v.getLatitude());
                        event.put("longitude", v.getLongitude());
                        event.put("statut", v.getStatut());
                        event.put("action", "POSITION_UPDATE");
                        event.put("timestamp", java.time.LocalDateTime.now().toString());
                        messagingTemplate.convertAndSend("/topic/detections", event);
                        
                        return ResponseEntity.ok(saved);
                    }
                    return ResponseEntity.badRequest().body(v);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
