package com.norival.norival_backend.domain.repository;

import com.norival.norival_backend.domain.model.Vehicule;
import java.util.List;
import java.util.Optional;

public interface VehiculeRepository {
    List<Vehicule> findAll();
    Optional<Vehicule> findById(Long id);
    Vehicule save(Vehicule vehicule);
    void deleteById(Long id);
}
