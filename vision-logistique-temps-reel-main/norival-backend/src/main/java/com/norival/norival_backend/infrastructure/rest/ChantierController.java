package com.norival.norival_backend.infrastructure.rest;

import com.norival.norival_backend.application.usecase.ChantierUseCase;
import com.norival.norival_backend.domain.model.Chantier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chantiers")
@CrossOrigin(origins = "*")
public class ChantierController {

    private final ChantierUseCase chantierUseCase;
    private final com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository jpaVehiculeRepository;
    private final com.norival.norival_backend.infrastructure.config.ConfigAuditHelper auditHelper;

    public ChantierController(ChantierUseCase chantierUseCase, 
                              com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository jpaVehiculeRepository,
                              com.norival.norival_backend.infrastructure.config.ConfigAuditHelper auditHelper) {
        this.chantierUseCase = chantierUseCase;
        this.jpaVehiculeRepository = jpaVehiculeRepository;
        this.auditHelper = auditHelper;
    }

    @GetMapping
    public List<Chantier> getChantiers() {
        return chantierUseCase.obtenirTousLesChantiers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Chantier> getChantierById(@PathVariable Long id) {
        return chantierUseCase.obtenirChantierParId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Chantier createChantier(@RequestBody Chantier chantier) {
        Chantier saved = chantierUseCase.enregistrerChantier(chantier);
        auditHelper.log("CREATE", "CHANTIER", saved.getId());
        return saved;
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateChantier(@PathVariable Long id, @RequestBody Chantier chantier) {
        if (chantier.getStatut() != null && chantier.getStatut().equalsIgnoreCase("CLOTURE")) {
            if (jpaVehiculeRepository.existsByChantierId(id)) {
                return ResponseEntity.badRequest().body("RG-06: Un chantier ne peut être clôturé dans le système que si l'ensemble de sa flotte affectée a été réaffectée ou libérée.");
            }
        }
        return chantierUseCase.obtenirChantierParId(id)
                .map(existing -> {
                    chantier.setId(id);
                    Chantier saved = chantierUseCase.enregistrerChantier(chantier);
                    auditHelper.log("UPDATE", "CHANTIER", saved.getId());
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChantier(@PathVariable Long id) {
        chantierUseCase.supprimerChantier(id);
        auditHelper.log("DELETE", "CHANTIER", id);
        return ResponseEntity.noContent().build();
    }
}
