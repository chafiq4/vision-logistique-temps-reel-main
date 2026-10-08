package com.norival.norival_backend.infrastructure.rest;

import com.norival.norival_backend.infrastructure.persistence.entity.MessageEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataMessageRepository;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin("*") // Autoriser les requêtes CORS depuis le frontend
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final SpringDataMessageRepository messageRepository;

    public ChatController(SimpMessagingTemplate messagingTemplate, SpringDataMessageRepository messageRepository) {
        this.messagingTemplate = messagingTemplate;
        this.messageRepository = messageRepository;
    }

    // Endpoint WebSocket pour recevoir les messages liés à un chantier
    @MessageMapping("/chat/{chantierId}")
    public void sendMessage(@DestinationVariable Long chantierId, MessageEntity message) {
        message.setChantierId(chantierId);
        message.setTimestamp(LocalDateTime.now());
        
        // 1. Sauvegarder dans la base de données
        MessageEntity savedMessage = messageRepository.save(message);

        // 2. Diffuser le message aux abonnés du topic
        messagingTemplate.convertAndSend("/topic/chat/" + chantierId, savedMessage);
    }

    // Endpoint REST pour récupérer l'historique des messages d'un chantier
    @GetMapping("/history/{chantierId}")
    public List<MessageEntity> getChatHistory(@PathVariable Long chantierId) {
        return messageRepository.findByChantierIdOrderByTimestampAsc(chantierId);
    }
}
