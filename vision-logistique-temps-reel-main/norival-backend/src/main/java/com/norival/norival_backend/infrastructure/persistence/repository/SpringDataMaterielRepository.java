package com.norival.norival_backend.infrastructure.persistence.repository;

import com.norival.norival_backend.infrastructure.persistence.entity.MaterielEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMaterielRepository extends JpaRepository<MaterielEntity, Long> {
    List<MaterielEntity> findByChantierId(Long chantierId);
}
