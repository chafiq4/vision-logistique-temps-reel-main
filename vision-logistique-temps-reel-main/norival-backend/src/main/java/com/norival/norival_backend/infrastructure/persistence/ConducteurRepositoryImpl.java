package com.norival.norival_backend.infrastructure.persistence;

import com.norival.norival_backend.domain.model.Conducteur;
import com.norival.norival_backend.domain.repository.ConducteurRepository;
import com.norival.norival_backend.infrastructure.persistence.entity.ConducteurEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataConducteurRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ConducteurRepositoryImpl implements ConducteurRepository {

    private final SpringDataConducteurRepository jpaRepository;

    public ConducteurRepositoryImpl(SpringDataConducteurRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Conducteur> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Conducteur> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Conducteur save(Conducteur conducteur) {
        ConducteurEntity entity = toEntity(conducteur);
        ConducteurEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Conducteur toDomain(ConducteurEntity entity) {
        return new Conducteur(entity.getId(), entity.getNom(), entity.getPrenom(), entity.getTelephone(), entity.getActif(), entity.getEmail(), entity.getPassword(), entity.getRole(), entity.getChantierId());
    }

    private ConducteurEntity toEntity(Conducteur domain) {
        return new ConducteurEntity(domain.getId(), domain.getNom(), domain.getPrenom(), domain.getTelephone(), domain.getActif(), domain.getEmail(), domain.getPassword(), domain.getRole(), domain.getChantierId());
    }
}
