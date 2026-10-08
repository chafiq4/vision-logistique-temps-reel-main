package com.norival.norival_backend.infrastructure.persistence.repository;

import com.norival.norival_backend.infrastructure.persistence.entity.RecommandationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataRecommandationRepository extends JpaRepository<RecommandationEntity, Long> {
    List<RecommandationEntity> findByStatut(String statut);
}
