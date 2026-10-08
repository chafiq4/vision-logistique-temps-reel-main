package com.norival.norival_backend.infrastructure.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuration_logs")
public class ConfigurationLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String action; // CREATE, UPDATE, DELETE

    @Column(nullable = false)
    private String referential; // CHANTIER, VEHICULE, CONDUCTEUR, MATERIEL

    @Column(name = "referential_id")
    private Long referentialId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ConfigurationLogEntity() {}

    public ConfigurationLogEntity(Long id, String username, String action, String referential, Long referentialId, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.action = action;
        this.referential = referential;
        this.referentialId = referentialId;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getReferential() { return referential; }
    public void setReferential(String referential) { this.referential = referential; }

    public Long getReferentialId() { return referentialId; }
    public void setReferentialId(Long referentialId) { this.referentialId = referentialId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
