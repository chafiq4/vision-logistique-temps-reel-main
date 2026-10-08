package com.norival.norival_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rotations")
public class RotationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicule_matricule", nullable = false)
    private String vehiculeMatricule;

    @Column(name = "type_engin")
    private String typeEngin;

    @Column(name = "chantier_id", nullable = false)
    private Long chantierId;

    @Column(nullable = false)
    private String action; // ENTREE or SORTIE

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public RotationEntity() {}

    public RotationEntity(String vehiculeMatricule, String typeEngin, Long chantierId, String action, LocalDateTime timestamp) {
        this.vehiculeMatricule = vehiculeMatricule;
        this.typeEngin = typeEngin;
        this.chantierId = chantierId;
        this.action = action;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getVehiculeMatricule() { return vehiculeMatricule; }
    public void setVehiculeMatricule(String vehiculeMatricule) { this.vehiculeMatricule = vehiculeMatricule; }

    public String getTypeEngin() { return typeEngin; }
    public void setTypeEngin(String typeEngin) { this.typeEngin = typeEngin; }

    public Long getChantierId() { return chantierId; }
    public void setChantierId(Long chantierId) { this.chantierId = chantierId; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
