package com.norival.norival_backend.infrastructure.persistence.repository;

import com.norival.norival_backend.infrastructure.persistence.entity.ChantierEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataChantierRepository extends JpaRepository<ChantierEntity, Long> {
}
