package com.norival.norival_backend.infrastructure.rest;

import com.norival.norival_backend.application.usecase.ConducteurUseCase;
import com.norival.norival_backend.domain.model.Conducteur;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conducteurs")
@CrossOrigin(origins = "*")
public class ConducteurController {

    private final ConducteurUseCase conducteurUseCase;
    private final com.norival.norival_backend.application.usecase.EmailService emailService;
    private final com.norival.norival_backend.infrastructure.config.ConfigAuditHelper auditHelper;

    public ConducteurController(ConducteurUseCase conducteurUseCase, 
                                com.norival.norival_backend.application.usecase.EmailService emailService,
                                com.norival.norival_backend.infrastructure.config.ConfigAuditHelper auditHelper) {
        this.conducteurUseCase = conducteurUseCase;
        this.emailService = emailService;
        this.auditHelper = auditHelper;
    }

    @GetMapping
    public List<Conducteur> getConducteurs() {
        return conducteurUseCase.obtenirTousLesConducteurs();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Conducteur> getConducteurById(@PathVariable Long id) {
        return conducteurUseCase.obtenirConducteurParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Conducteur createConducteur(@RequestBody Conducteur conducteur) {
        Conducteur saved = conducteurUseCase.enregistrerConducteur(conducteur);
        if (saved.getEmail() != null && !saved.getEmail().isEmpty()) {
            emailService.sendCredentialsEmail(saved.getEmail(), saved.getPrenom() + " " + saved.getNom(), saved.getPassword());
        }
        auditHelper.log("CREATE", "CONDUCTEUR", saved.getId());
        return saved;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Conducteur> updateConducteur(@PathVariable Long id, @RequestBody Conducteur conducteur) {
        return conducteurUseCase.obtenirConducteurParId(id)
                .map(existing -> {
                    conducteur.setId(id);
                    Conducteur saved = conducteurUseCase.enregistrerConducteur(conducteur);
                    auditHelper.log("UPDATE", "CONDUCTEUR", saved.getId());
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConducteur(@PathVariable Long id) {
        conducteurUseCase.supprimerConducteur(id);
        auditHelper.log("DELETE", "CONDUCTEUR", id);
        return ResponseEntity.noContent().build();
    }
}
