package com.norival.norival_backend.application.usecase;

import com.norival.norival_backend.domain.model.Conducteur;
import com.norival.norival_backend.domain.repository.ConducteurRepository;
import java.util.List;
import java.util.Optional;

public class ConducteurUseCase {
    private final ConducteurRepository conducteurRepository;

    public ConducteurUseCase(ConducteurRepository conducteurRepository) {
        this.conducteurRepository = conducteurRepository;
    }

    public List<Conducteur> obtenirTousLesConducteurs() {
        return conducteurRepository.findAll();
    }

    public Optional<Conducteur> obtenirConducteurParId(Long id) {
        return conducteurRepository.findById(id);
    }

    public Conducteur enregistrerConducteur(Conducteur conducteur) {
        return conducteurRepository.save(conducteur);
    }

    public void supprimerConducteur(Long id) {
        conducteurRepository.deleteById(id);
    }
}
