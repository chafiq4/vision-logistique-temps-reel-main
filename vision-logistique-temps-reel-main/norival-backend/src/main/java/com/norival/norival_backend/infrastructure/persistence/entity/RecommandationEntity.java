package com.norival.norival_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "recommandations")
public class RecommandationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicule_id", nullable = false)
    private Long vehiculeId;

    @Column(name = "vehicule_matricule")
    private String vehiculeMatricule;

    @Column(name = "target_chantier_id", nullable = false)
    private Long targetChantierId;

    @Column(name = "target_chantier_nom")
    private String targetChantierNom;

    @Column(nullable = false)
    private String statut; // PROPOSEE, VALIDEE, REJETEE

    @Column(name = "gain_estime")
    private Double gainEstime;

    @Column(name = "motif_rejet")
    private String motifRejet;

    @Column(name = "date_proposition", nullable = false)
    private LocalDateTime dateProposition;

    public RecommandationEntity() {}

    public RecommandationEntity(Long id, Long vehiculeId, String vehiculeMatricule, Long targetChantierId, String targetChantierNom, String statut, Double gainEstime, String motifRejet, LocalDateTime dateProposition) {
        this.id = id;
        this.vehiculeId = vehiculeId;
        this.vehiculeMatricule = vehiculeMatricule;
        this.targetChantierId = targetChantierId;
        this.targetChantierNom = targetChantierNom;
        this.statut = statut;
        this.gainEstime = gainEstime;
        this.motifRejet = motifRejet;
        this.dateProposition = dateProposition;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getVehiculeId() { return vehiculeId; }
    public void setVehiculeId(Long vehiculeId) { this.vehiculeId = vehiculeId; }

    public String getVehiculeMatricule() { return vehiculeMatricule; }
    public void setVehiculeMatricule(String vehiculeMatricule) { this.vehiculeMatricule = vehiculeMatricule; }

    public Long getTargetChantierId() { return targetChantierId; }
    public void setTargetChantierId(Long targetChantierId) { this.targetChantierId = targetChantierId; }

    public String getTargetChantierNom() { return targetChantierNom; }
    public void setTargetChantierNom(String targetChantierNom) { this.targetChantierNom = targetChantierNom; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Double getGainEstime() { return gainEstime; }
    public void setGainEstime(Double gainEstime) { this.gainEstime = gainEstime; }

    public String getMotifRejet() { return motifRejet; }
    public void setMotifRejet(String motifRejet) { this.motifRejet = motifRejet; }

    public LocalDateTime getDateProposition() { return dateProposition; }
    public void setDateProposition(LocalDateTime dateProposition) { this.dateProposition = dateProposition; }
}
