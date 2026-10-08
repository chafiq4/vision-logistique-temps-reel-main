package com.norival.norival_backend.infrastructure.rest;

import com.norival.norival_backend.infrastructure.persistence.entity.AlerteEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataAlerteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlerteController {

    private final SpringDataAlerteRepository repository;

    public AlerteController(SpringDataAlerteRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<AlerteEntity> getAlerts() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<AlerteEntity> createAlert(@RequestBody AlerteEntity alert) {
        if (alert.getTimestamp() == null) {
            alert.setTimestamp(java.time.LocalDateTime.now());
        }
        return ResponseEntity.ok(repository.save(alert));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id, 
            @RequestParam String status, 
            @RequestParam(required = false) String role) {
        return repository.findById(id)
                .map(existing -> {
                    // Enforce RG-09: a resolved/closed alert ("CLOTUREE") cannot be reopened except by HSE or ADMIN
                    if (existing.getStatut().equals("CLOTUREE") && !status.equals("CLOTUREE")) {
                        if (role == null || (!role.equalsIgnoreCase("ADMIN") && !role.equalsIgnoreCase("HSE") && !role.equalsIgnoreCase("HSE_AGENT"))) {
                            return ResponseEntity.badRequest().body("RG-09: Une alerte clôturée ne peut être réouverte que par un Agent HSE ou un Administrateur.");
                        }
                    }
                    existing.setStatut(status);
                    return ResponseEntity.ok(repository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
