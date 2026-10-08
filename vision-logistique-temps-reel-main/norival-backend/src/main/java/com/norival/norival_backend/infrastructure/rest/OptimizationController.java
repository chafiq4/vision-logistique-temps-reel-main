package com.norival.norival_backend.infrastructure.rest;

import com.norival.norival_backend.application.usecase.OptimizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/optimization")
@CrossOrigin(origins = "*")
public class OptimizationController {

    private final OptimizationService optimizationService;

    public OptimizationController(OptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }

    @GetMapping("/optimize")
    public ResponseEntity<List<Map<String, Object>>> getOptimizedAssignments() {
        List<Map<String, Object>> result = optimizationService.optimiserAffectation();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/recommandations")
    public ResponseEntity<List<com.norival.norival_backend.infrastructure.persistence.entity.RecommandationEntity>> getRecommandations() {
        return ResponseEntity.ok(optimizationService.obtenirToutesLesRecommandations());
    }

    @PostMapping("/recommandations/{id}/valider")
    public ResponseEntity<Void> validerRecommandation(@PathVariable Long id) {
        boolean success = optimizationService.validerRecommandation(id);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/recommandations/{id}/rejeter")
    public ResponseEntity<Void> rejeterRecommandation(@PathVariable Long id, @RequestParam String motif) {
        try {
            boolean success = optimizationService.rejeterRecommandation(id, motif);
            if (success) {
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
