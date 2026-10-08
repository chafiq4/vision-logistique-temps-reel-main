package com.norival.norival_backend.infrastructure.rest;

import com.norival.norival_backend.domain.model.VideoArchive;
import com.norival.norival_backend.domain.repository.VideoArchiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
@CrossOrigin(origins = "*")
public class VideoController {

    private final String UPLOAD_DIR = "uploads/videos/";

    @Autowired
    private VideoArchiveRepository repository;

    @Autowired
    private org.springframework.messaging.simp.SimpMessagingTemplate messagingTemplate;

    @GetMapping
    public List<VideoArchive> getAllVideos() {
        return repository.findAll();
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadVideo(@RequestParam("file") MultipartFile file,
                                         @RequestParam("title") String title,
                                         @RequestParam("status") String status,
                                         @RequestParam("duration") String duration) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Fichier vide");
        }

        try {
            // Créer le dossier s'il n'existe pas
            File dir = new File(UPLOAD_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Générer un nom unique
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path path = Paths.get(UPLOAD_DIR + fileName);
            
            // Sauvegarder le fichier
            Files.write(path, file.getBytes());

            // Construire l'URL
            String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/uploads/videos/")
                    .path(fileName)
                    .toUriString();

            // Créer l'entité
            VideoArchive archive = new VideoArchive(title, duration, LocalDateTime.now(), fileDownloadUri, status);
            repository.save(archive);
            
            messagingTemplate.convertAndSend("/topic/videos", archive);

            return ResponseEntity.ok(archive);

        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Erreur lors de la sauvegarde: " + e.getMessage());
        }
    }
}
