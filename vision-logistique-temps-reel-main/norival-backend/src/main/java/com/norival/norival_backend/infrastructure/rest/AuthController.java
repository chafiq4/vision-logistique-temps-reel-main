package com.norival.norival_backend.infrastructure.rest;

import com.norival.norival_backend.infrastructure.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final JwtTokenProvider tokenProvider;
    private final com.norival.norival_backend.infrastructure.persistence.repository.SpringDataConducteurRepository conducteurRepository;
    private final com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository vehiculeRepository;
    private final com.norival.norival_backend.infrastructure.persistence.repository.SpringDataChantierRepository chantierRepository;

    public AuthController(
            JwtTokenProvider tokenProvider,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataConducteurRepository conducteurRepository,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataVehiculeRepository vehiculeRepository,
            com.norival.norival_backend.infrastructure.persistence.repository.SpringDataChantierRepository chantierRepository) {
        this.tokenProvider = tokenProvider;
        this.conducteurRepository = conducteurRepository;
        this.vehiculeRepository = vehiculeRepository;
        this.chantierRepository = chantierRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        // Simulation d'identifiants administrateur
        if ("admin".equals(username) && "admin".equals(password)) {
            String token = tokenProvider.generateToken(username);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("type", "Bearer");
            response.put("role", "ADMIN");
            return ResponseEntity.ok(response);
        }

        // Rechercher si l'utilisateur existe en BDD
        java.util.List<com.norival.norival_backend.infrastructure.persistence.entity.ConducteurEntity> list = 
            conducteurRepository.findAll();
        for (com.norival.norival_backend.infrastructure.persistence.entity.ConducteurEntity cond : list) {
            if (cond.getEmail() != null && cond.getEmail().equalsIgnoreCase(username) &&
                cond.getPassword() != null && cond.getPassword().equals(password)) {
                String token = tokenProvider.generateToken(username);
                Map<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("type", "Bearer");
                
                String role = cond.getRole() != null ? cond.getRole() : "DRIVER";
                response.put("role", role);
                response.put("nomComplet", cond.getPrenom() + " " + cond.getNom());
                response.put("conducteurId", cond.getId().toString());
                
                if (cond.getChantierId() != null) {
                    response.put("chantierId", cond.getChantierId().toString());
                    String chantierNom = chantierRepository.findById(cond.getChantierId())
                        .map(com.norival.norival_backend.infrastructure.persistence.entity.ChantierEntity::getNom)
                        .orElse("Chantier Inconnu");
                    response.put("chantierNom", chantierNom);
                } else {
                    response.put("chantierId", "");
                    response.put("chantierNom", "");
                }

                if ("DRIVER".equalsIgnoreCase(role)) {
                    // Trouver le véhicule assigné au chauffeur
                    java.util.List<com.norival.norival_backend.infrastructure.persistence.entity.VehiculeEntity> vehiList = 
                        vehiculeRepository.findAll();
                    for (com.norival.norival_backend.infrastructure.persistence.entity.VehiculeEntity v : vehiList) {
                        if (cond.getId().equals(v.getConducteurId())) {
                            response.put("vehiculeId", v.getId().toString());
                            response.put("vehiculeMatricule", v.getMatricule());
                            response.put("vehiculeStatut", v.getStatut());
                            break;
                        }
                    }
                }
                
                return ResponseEntity.ok(response);
            }
        }

        return ResponseEntity.status(401).build();
    }
}
