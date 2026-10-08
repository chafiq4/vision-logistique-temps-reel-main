package com.norival.norival_backend.infrastructure.persistence.repository;

import com.norival.norival_backend.infrastructure.persistence.entity.ConfigurationLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataConfigurationLogRepository extends JpaRepository<ConfigurationLogEntity, Long> {
}
