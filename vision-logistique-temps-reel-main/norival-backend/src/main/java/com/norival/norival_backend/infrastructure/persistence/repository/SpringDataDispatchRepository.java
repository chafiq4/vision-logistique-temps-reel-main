package com.norival.norival_backend.infrastructure.persistence.repository;

import com.norival.norival_backend.infrastructure.persistence.entity.DispatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataDispatchRepository extends JpaRepository<DispatchEntity, Long> {
    List<DispatchEntity> findByReceiverId(Long receiverId);
    List<DispatchEntity> findBySenderId(Long senderId);
}
