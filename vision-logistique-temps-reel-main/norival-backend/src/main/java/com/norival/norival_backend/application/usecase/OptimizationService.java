package com.norival.norival_backend.application.usecase;

import com.norival.norival_backend.domain.model.Chantier;
import com.norival.norival_backend.domain.model.Vehicule;
import com.norival.norival_backend.domain.repository.ChantierRepository;
import com.norival.norival_backend.domain.repository.VehiculeRepository;
import com.norival.norival_backend.infrastructure.persistence.entity.RecommandationEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataRecommandationRepository;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OptimizationService {

    private final ChantierRepository chantierRepository;
    private final VehiculeRepository vehiculeRepository;
    private final SpringDataRecommandationRepository recommandationRepository;
    private final SpringDataVehiculeRepository jpaVehiculeRepository;

    public OptimizationService(ChantierRepository chantierRepository, 
                               VehiculeRepository vehiculeRepository,
                               SpringDataRecommandationRepository recommandationRepository,
                               SpringDataVehiculeRepository jpaVehiculeRepository) {
        this.chantierRepository = chantierRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.recommandationRepository = recommandationRepository;
        this.jpaVehiculeRepository = jpaVehiculeRepository;
    }

    /**
     * Résout l'affectation optimale des véhicules disponibles aux chantiers
     * en minimisant la distance Haversine en Kilomètres.
     */
    public List<Map<String, Object>> optimiserAffectation() {
        List<Chantier> chantiers = chantierRepository.findAll();
        List<Vehicule> vehicules = vehiculeRepository.findAll();

        // Prendre tous les véhicules disponibles (avec GPS)
        List<Vehicule> disponibles = vehicules.stream()
                .filter(v -> v.getLatitude() != null && v.getLongitude() != null)
                .filter(v -> "DISPONIBLE".equalsIgnoreCase(v.getStatut()))
                .toList();

        List<Map<String, Object>> affectations = new ArrayList<>();
        List<Vehicule> vehiculesAttribues = new ArrayList<>();

        // Clear existing recommendations in 'PROPOSEE' status to recalculate new ones cleanly
        List<RecommandationEntity> existingProposed = recommandationRepository.findByStatut("PROPOSEE");
        recommandationRepository.deleteAll(existingProposed);

        for (Chantier chantier : chantiers) {
            // RG-06: Don't optimize or suggest assignments to closed chantiers
            if (chantier.getStatut() != null && chantier.getStatut().equals("CLOTURE")) {
                continue;
            }

            Vehicule meilleurVehicule = null;
            double distanceMinKm = Double.MAX_VALUE;

            for (Vehicule vehicule : disponibles) {
                if (vehiculesAttribues.contains(vehicule)) {
                    continue;
                }

                // Ne pas recommander un véhicule pour le chantier où il se trouve déjà
                if (vehicule.getChantierId() != null && vehicule.getChantierId().equals(chantier.getId())) {
                    continue;
                }

                // Calcul de la distance réelle Haversine en km
                double distanceKm = calcularHaversineDistance(
                        chantier.getLatitude(), chantier.getLongitude(),
                        vehicule.getLatitude(), vehicule.getLongitude()
                );

                if (distanceKm < distanceMinKm) {
                    distanceMinKm = distanceKm;
                    meilleurVehicule = vehicule;
                }
            }

            if (meilleurVehicule != null) {
                vehiculesAttribues.add(meilleurVehicule);

                // Save recommendation to Database (RG-04)
                RecommandationEntity rec = new RecommandationEntity(
                    null,
                    meilleurVehicule.getId(),
                    meilleurVehicule.getMatricule(),
                    chantier.getId(),
                    chantier.getNom(),
                    "PROPOSEE",
                    distanceMinKm,
                    null,
                    LocalDateTime.now()
                );
                rec = recommandationRepository.save(rec);

                Map<String, Object> affectation = new HashMap<>();
                affectation.put("id", rec.getId()); // Pass DB recommendation ID
                affectation.put("chantierId", chantier.getId());
                affectation.put("chantierNom", chantier.getNom());
                affectation.put("vehiculeId", meilleurVehicule.getId());
                affectation.put("vehiculeMatricule", meilleurVehicule.getMatricule());
                affectation.put("vehiculeType", meilleurVehicule.getType());
                affectation.put("distanceEstimeeDegree", distanceMinKm);
                affectation.put("statut", "PROPOSEE");
                affectations.add(affectation);
            }
        }

        return affectations;
    }

    public List<RecommandationEntity> obtenirToutesLesRecommandations() {
        return recommandationRepository.findAll();
    }

    /**
     * Valide une recommandation (RG-04). Affecte le véhicule au chantier.
     */
    public boolean validerRecommandation(Long id) {
        Optional<RecommandationEntity> recOpt = recommandationRepository.findById(id);
        if (recOpt.isPresent()) {
            RecommandationEntity rec = recOpt.get();
            rec.setStatut("VALIDEE");
            recommandationRepository.save(rec);

            // Appliquer la réaffectation effective (RG-01 : affectation à un seul chantier actif)
            var vehiculeOpt = jpaVehiculeRepository.findById(rec.getVehiculeId());
            if (vehiculeOpt.isPresent()) {
                var vehicule = vehiculeOpt.get();
                vehicule.setChantierId(rec.getTargetChantierId());
                jpaVehiculeRepository.save(vehicule);
            }
            return true;
        }
        return false;
    }

    /**
     * Rejette une recommandation avec motif obligatoire (RG-12).
     */
    public boolean rejeterRecommandation(Long id, String motif) {
        if (motif == null || motif.trim().isEmpty()) {
            throw new IllegalArgumentException("Le motif de rejet est obligatoire.");
        }
        Optional<RecommandationEntity> recOpt = recommandationRepository.findById(id);
        if (recOpt.isPresent()) {
            RecommandationEntity rec = recOpt.get();
            rec.setStatut("REJETEE");
            rec.setMotifRejet(motif);
            recommandationRepository.save(rec);
            return true;
        }
        return false;
    }

    private double calcularHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}

