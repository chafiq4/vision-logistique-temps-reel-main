package com.norival.norival_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "dispatch_messages")
public class DispatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long senderId;
    private String senderNom;
    private Long receiverId;
    private String receiverNom;

    @Column(length = 1000)
    private String message;

    private LocalDateTime timestamp;
    private String statut; // EN_ATTENTE, EN_COURS, TERMINE

    private Long chantierId;
    private String chantierNom;

    public DispatchEntity() {}

    public DispatchEntity(Long senderId, String senderNom, Long receiverId, String receiverNom, String message, Long chantierId, String chantierNom) {
        this.senderId = senderId;
        this.senderNom = senderNom;
        this.receiverId = receiverId;
        this.receiverNom = receiverNom;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.statut = "EN_ATTENTE";
        this.chantierId = chantierId;
        this.chantierNom = chantierNom;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderNom() { return senderNom; }
    public void setSenderNom(String senderNom) { this.senderNom = senderNom; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getReceiverNom() { return receiverNom; }
    public void setReceiverNom(String receiverNom) { this.receiverNom = receiverNom; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public Long getChantierId() { return chantierId; }
    public void setChantierId(Long chantierId) { this.chantierId = chantierId; }

    public String getChantierNom() { return chantierNom; }
    public void setChantierNom(String chantierNom) { this.chantierNom = chantierNom; }
}
