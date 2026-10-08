package com.norival.norival_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "chantiers")
public class ChantierEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "rayon_geofence", nullable = false)
    private Double rayonGeofence;

    private String statut;
    private Double avancement;

    public ChantierEntity() {}

    public ChantierEntity(Long id, String nom, Double latitude, Double longitude, Double rayonGeofence) {
        this(id, nom, latitude, longitude, rayonGeofence, "ACTIF", 0.0);
    }

    public ChantierEntity(Long id, String nom, Double latitude, Double longitude, Double rayonGeofence, String statut, Double avancement) {
        this.id = id;
        this.nom = nom;
        this.latitude = latitude;
        this.longitude = longitude;
        this.rayonGeofence = rayonGeofence;
        this.statut = statut != null ? statut : "ACTIF";
        this.avancement = avancement != null ? avancement : 0.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getRayonGeofence() { return rayonGeofence; }
    public void setRayonGeofence(Double rayonGeofence) { this.rayonGeofence = rayonGeofence; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Double getAvancement() { return avancement; }
    public void setAvancement(Double avancement) { this.avancement = avancement; }
}
