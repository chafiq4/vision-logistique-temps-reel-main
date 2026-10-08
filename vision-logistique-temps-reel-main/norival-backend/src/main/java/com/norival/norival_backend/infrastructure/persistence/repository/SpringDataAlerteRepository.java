package com.norival.norival_backend.infrastructure.persistence.repository;

import com.norival.norival_backend.infrastructure.persistence.entity.AlerteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataAlerteRepository extends JpaRepository<AlerteEntity, Long> {
    List<AlerteEntity> findByChantierId(Long chantierId);
}
