package com.norival.norival_backend.infrastructure.rest;

import com.norival.norival_backend.infrastructure.persistence.entity.RotationEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataRotationRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rotations")
@CrossOrigin(origins = "*")
public class RotationController {

    private final SpringDataRotationRepository rotationRepository;

    public RotationController(SpringDataRotationRepository rotationRepository) {
        this.rotationRepository = rotationRepository;
    }

    @GetMapping
    public ResponseEntity<List<RotationEntity>> getRotationHistory() {
        // Retourne toutes les rotations triées de la plus récente à la plus ancienne
        List<RotationEntity> rotations = rotationRepository.findAll();
        return ResponseEntity.ok(rotations);
    }
}
