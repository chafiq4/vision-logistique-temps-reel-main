package com.norival.norival_backend.infrastructure.persistence.repository;

import com.norival.norival_backend.infrastructure.persistence.entity.VehiculeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataVehiculeRepository extends JpaRepository<VehiculeEntity, Long> {
    boolean existsByMatricule(String matricule);
    java.util.Optional<VehiculeEntity> findByMatricule(String matricule);
    boolean existsByChantierId(Long chantierId);
    boolean existsByConducteurId(Long conducteurId);
    boolean existsByConducteurIdAndIdNot(Long conducteurId, Long id);
}
