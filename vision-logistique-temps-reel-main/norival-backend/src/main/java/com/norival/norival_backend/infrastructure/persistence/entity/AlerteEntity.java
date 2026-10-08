package com.norival.norival_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alertes")
public class AlerteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type; // HSE, LOGISTIQUE

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private String statut; // ACTIVE, RESOLUE, CLOTUREE

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "chantier_id")
    private Long chantierId;

    private String matricule;

    @Column(nullable = false)
    private String criticite; // NORMAL, SURVEILLER, CRITIQUE

    public AlerteEntity() {}

    public AlerteEntity(Long id, String type, String message, String statut, LocalDateTime timestamp, Long chantierId, String matricule, String criticite) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.statut = statut;
        this.timestamp = timestamp;
        this.chantierId = chantierId;
        this.matricule = matricule;
        this.criticite = criticite;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getChantierId() { return chantierId; }
    public void setChantierId(Long chantierId) { this.chantierId = chantierId; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getCriticite() { return criticite; }
    public void setCriticite(String criticite) { this.criticite = criticite; }
}
