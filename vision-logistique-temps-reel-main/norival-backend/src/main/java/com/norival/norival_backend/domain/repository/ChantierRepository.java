package com.norival.norival_backend.domain.repository;

import com.norival.norival_backend.domain.model.Chantier;
import java.util.List;
import java.util.Optional;

public interface ChantierRepository {
    List<Chantier> findAll();
    Optional<Chantier> findById(Long id);
    Chantier save(Chantier chantier);
    void deleteById(Long id);
}
