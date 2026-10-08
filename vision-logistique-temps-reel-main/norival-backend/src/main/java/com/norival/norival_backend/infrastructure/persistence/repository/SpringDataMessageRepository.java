package com.norival.norival_backend.infrastructure.persistence.repository;

import com.norival.norival_backend.infrastructure.persistence.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpringDataMessageRepository extends JpaRepository<MessageEntity, Long> {
    
    // Pour récupérer les messages liés à un chantier précis
    List<MessageEntity> findByChantierIdOrderByTimestampAsc(Long chantierId);
    
    // Pour récupérer les messages entre deux utilisateurs
    List<MessageEntity> findBySenderIdAndReceiverIdOrSenderIdAndReceiverIdOrderByTimestampAsc(
        String senderId1, String receiverId1, String senderId2, String receiverId2
    );
}
