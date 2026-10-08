package com.norival.norival_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vehicules")
public class VehiculeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String matricule;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String statut;

    private Double latitude;
    private Double longitude;

    @Column(name = "conducteur_id")
    private Long conducteurId;

    @Column(name = "chantier_id")
    private Long chantierId;

    public VehiculeEntity() {}

    public VehiculeEntity(Long id, String matricule, String type, String statut, Double latitude, Double longitude) {
        this(id, matricule, type, statut, latitude, longitude, null, null);
    }

    public VehiculeEntity(Long id, String matricule, String type, String statut, Double latitude, Double longitude, Long conducteurId) {
        this(id, matricule, type, statut, latitude, longitude, conducteurId, null);
    }

    public VehiculeEntity(Long id, String matricule, String type, String statut, Double latitude, Double longitude, Long conducteurId, Long chantierId) {
        this.id = id;
        this.matricule = matricule;
        this.type = type;
        this.statut = statut;
        this.latitude = latitude;
        this.longitude = longitude;
        this.conducteurId = conducteurId;
        this.chantierId = chantierId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMatricule() { return matricule; }
    public void setMatricule(String matricule) { this.matricule = matricule; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Long getConducteurId() { return conducteurId; }
    public void setConducteurId(Long conducteurId) { this.conducteurId = conducteurId; }

    public Long getChantierId() { return chantierId; }
    public void setChantierId(Long chantierId) { this.chantierId = chantierId; }
}
