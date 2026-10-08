package com.norival.norival_backend.infrastructure.persistence;

import com.norival.norival_backend.domain.model.Vehicule;
import com.norival.norival_backend.domain.repository.VehiculeRepository;
import com.norival.norival_backend.infrastructure.persistence.entity.VehiculeEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class VehiculeRepositoryImpl implements VehiculeRepository {

    private final SpringDataVehiculeRepository jpaRepository;

    public VehiculeRepositoryImpl(SpringDataVehiculeRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Vehicule> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Vehicule> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Vehicule save(Vehicule vehicule) {
        VehiculeEntity entity = toEntity(vehicule);
        VehiculeEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Vehicule toDomain(VehiculeEntity entity) {
        return new Vehicule(entity.getId(), entity.getMatricule(), entity.getType(), entity.getStatut(), entity.getLatitude(), entity.getLongitude(), entity.getConducteurId(), entity.getChantierId());
    }

    private VehiculeEntity toEntity(Vehicule domain) {
        return new VehiculeEntity(domain.getId(), domain.getMatricule(), domain.getType(), domain.getStatut(), domain.getLatitude(), domain.getLongitude(), domain.getConducteurId(), domain.getChantierId());
    }
}
