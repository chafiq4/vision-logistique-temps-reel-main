package com.norival.norival_backend.infrastructure.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = "*")
public class AiController {

    private static final Logger logger = LoggerFactory.getLogger(AiController.class);

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startAiProcess() {
        try {
            logger.info("Tentative de lancement du processus de simulation d'IA Python...");

            // Utiliser 'cmd /c start' pour forcer l'ouverture d'une fenêtre visible sous Windows
            // Use -u for unbuffered output so prints show up immediately in the black window
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "python", "-u", "main.py");
            // Définir le répertoire de travail vers le dossier norival-ai
            pb.directory(new File("c:\\Users\\pc\\Desktop\\stage_4iir\\norival-ai"));
            
            // Lancer le script de manière asynchrone sans bloquer le serveur Spring Boot
            pb.start();

            logger.info("Processus IA Python lancé avec succès.");
            return ResponseEntity.ok(Collections.singletonMap("status", "Simulation d'IA lancée avec succès"));

        } catch (Exception e) {
            logger.error("Erreur lors du lancement de la simulation IA: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(Collections.singletonMap("error", "Impossible de démarrer l'IA: " + e.getMessage()));
        }
    }
}
