package com.norival.norival_backend.application.usecase;

import com.norival.norival_backend.domain.model.Chantier;
import com.norival.norival_backend.domain.repository.ChantierRepository;
import java.util.List;
import java.util.Optional;

public class ChantierUseCase {
    private final ChantierRepository chantierRepository;

    public ChantierUseCase(ChantierRepository chantierRepository) {
        this.chantierRepository = chantierRepository;
    }

    public List<Chantier> obtenirTousLesChantiers() {
        return chantierRepository.findAll();
    }

    public Optional<Chantier> obtenirChantierParId(Long id) {
        return chantierRepository.findById(id);
    }

    public Chantier enregistrerChantier(Chantier chantier) {
        return chantierRepository.save(chantier);
    }

    public void supprimerChantier(Long id) {
        chantierRepository.deleteById(id);
    }
}
