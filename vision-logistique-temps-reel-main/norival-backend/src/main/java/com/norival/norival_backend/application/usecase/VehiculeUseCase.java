package com.norival.norival_backend.application.usecase;

import com.norival.norival_backend.domain.model.Vehicule;
import com.norival.norival_backend.domain.repository.VehiculeRepository;
import java.util.List;
import java.util.Optional;

public class VehiculeUseCase {
    private final VehiculeRepository vehiculeRepository;

    public VehiculeUseCase(VehiculeRepository vehiculeRepository) {
        this.vehiculeRepository = vehiculeRepository;
    }

    public List<Vehicule> obtenirTousLesVehicules() {
        return vehiculeRepository.findAll();
    }

    public Optional<Vehicule> obtenirVehiculeParId(Long id) {
        return vehiculeRepository.findById(id);
    }

    public Vehicule enregistrerVehicule(Vehicule vehicule) {
        return vehiculeRepository.save(vehicule);
    }

    public void supprimerVehicule(Long id) {
        vehiculeRepository.deleteById(id);
    }
}
