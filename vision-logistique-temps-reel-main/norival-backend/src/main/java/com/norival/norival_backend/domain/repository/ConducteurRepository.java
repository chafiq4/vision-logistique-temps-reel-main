package com.norival.norival_backend.domain.repository;

import com.norival.norival_backend.domain.model.Conducteur;
import java.util.List;
import java.util.Optional;

public interface ConducteurRepository {
    List<Conducteur> findAll();
    Optional<Conducteur> findById(Long id);
    Conducteur save(Conducteur conducteur);
    void deleteById(Long id);
}
