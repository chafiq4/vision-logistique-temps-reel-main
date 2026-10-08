package com.norival.norival_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "materiel")
public class MaterielEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String reference;

    @Column(nullable = false)
    private String categorie;

    @Column(name = "quantite_disponible", nullable = false)
    private Integer quantiteDisponible;

    @Column(name = "chantier_id")
    private Long chantierId;

    public MaterielEntity() {}

    public MaterielEntity(Long id, String reference, String categorie, Integer quantiteDisponible, Long chantierId) {
        this.id = id;
        this.reference = reference;
        this.categorie = categorie;
        this.quantiteDisponible = quantiteDisponible;
        this.chantierId = chantierId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    public Integer getQuantiteDisponible() { return quantiteDisponible; }
    public void setQuantiteDisponible(Integer quantiteDisponible) { this.quantiteDisponible = quantiteDisponible; }

    public Long getChantierId() { return chantierId; }
    public void setChantierId(Long chantierId) { this.chantierId = chantierId; }
}
