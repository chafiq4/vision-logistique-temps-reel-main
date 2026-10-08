package com.norival.norival_backend.infrastructure.rest;

import com.norival.norival_backend.infrastructure.persistence.entity.MaterielEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataMaterielRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materiel")
@CrossOrigin(origins = "*")
public class MaterielController {

    private final SpringDataMaterielRepository repository;
    private final com.norival.norival_backend.infrastructure.config.ConfigAuditHelper auditHelper;

    public MaterielController(SpringDataMaterielRepository repository, 
                              com.norival.norival_backend.infrastructure.config.ConfigAuditHelper auditHelper) {
        this.repository = repository;
        this.auditHelper = auditHelper;
    }

    @GetMapping
    public List<MaterielEntity> getMateriel() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaterielEntity> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody MaterielEntity materiel) {
        // Enforce RG-07: reserved quantity cannot exceed quantity at central depot (where chantierId is null)
        if (materiel.getChantierId() != null) {
            List<MaterielEntity> allMateriel = repository.findAll();
            // Find equivalent equipment in central depot
            MaterielEntity centralDepotItem = allMateriel.stream()
                    .filter(m -> m.getChantierId() == null && m.getReference().equalsIgnoreCase(materiel.getReference()))
                    .findFirst()
                    .orElse(null);

            if (centralDepotItem == null) {
                return ResponseEntity.badRequest().body("RG-07: L'équipement n'existe pas au dépôt central. Réservation impossible.");
            }

            if (materiel.getQuantiteDisponible() > centralDepotItem.getQuantiteDisponible()) {
                return ResponseEntity.badRequest().body("RG-07: La quantité réservée (" + materiel.getQuantiteDisponible() 
                        + ") ne peut excéder la quantité disponible au dépôt central (" + centralDepotItem.getQuantiteDisponible() + ").");
            }
            
            // Deduct from central depot
            centralDepotItem.setQuantiteDisponible(centralDepotItem.getQuantiteDisponible() - materiel.getQuantiteDisponible());
            repository.save(centralDepotItem);
        }
        MaterielEntity saved = repository.save(materiel);
        auditHelper.log("CREATE", "MATERIEL", saved.getId());
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody MaterielEntity materiel) {
        return repository.findById(id)
                .map(existing -> {
                    // Enforce RG-07
                    if (materiel.getChantierId() != null) {
                        List<MaterielEntity> allMateriel = repository.findAll();
                        MaterielEntity centralDepotItem = allMateriel.stream()
                                .filter(m -> m.getChantierId() == null && m.getReference().equalsIgnoreCase(materiel.getReference()))
                                .findFirst()
                                .orElse(null);

                        if (centralDepotItem == null) {
                            return ResponseEntity.badRequest().body("RG-07: L'équipement n'existe pas au dépôt central.");
                        }

                        if (materiel.getQuantiteDisponible() > centralDepotItem.getQuantiteDisponible()) {
                            return ResponseEntity.badRequest().body("RG-07: La quantité réservée ne peut excéder celle du dépôt central.");
                        }
                    }
                    materiel.setId(id);
                    MaterielEntity saved = repository.save(materiel);
                    auditHelper.log("UPDATE", "MATERIEL", saved.getId());
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        repository.deleteById(id);
        auditHelper.log("DELETE", "MATERIEL", id);
        return ResponseEntity.noContent().build();
    }
}
