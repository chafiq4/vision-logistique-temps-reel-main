package com.norival.norival_backend.infrastructure.persistence;

import com.norival.norival_backend.domain.model.Chantier;
import com.norival.norival_backend.domain.repository.ChantierRepository;
import com.norival.norival_backend.infrastructure.persistence.entity.ChantierEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataChantierRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ChantierRepositoryImpl implements ChantierRepository {

    private final SpringDataChantierRepository jpaRepository;

    public ChantierRepositoryImpl(SpringDataChantierRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<Chantier> findAll() {
        return jpaRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Chantier> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Chantier save(Chantier chantier) {
        ChantierEntity entity = toEntity(chantier);
        ChantierEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    private Chantier toDomain(ChantierEntity entity) {
        return new Chantier(entity.getId(), entity.getNom(), entity.getLatitude(), entity.getLongitude(), entity.getRayonGeofence(), entity.getStatut(), entity.getAvancement());
    }

    private ChantierEntity toEntity(Chantier domain) {
        return new ChantierEntity(domain.getId(), domain.getNom(), domain.getLatitude(), domain.getLongitude(), domain.getRayonGeofence(), domain.getStatut(), domain.getAvancement());
    }
}
