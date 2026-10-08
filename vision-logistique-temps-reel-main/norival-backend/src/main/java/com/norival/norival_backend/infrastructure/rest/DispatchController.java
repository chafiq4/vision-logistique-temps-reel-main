package com.norival.norival_backend.infrastructure.rest;

import com.norival.norival_backend.infrastructure.persistence.entity.DispatchEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataDispatchRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispatch")
@CrossOrigin(origins = "*")
public class DispatchController {

    private final SpringDataDispatchRepository repository;
    private final SimpMessagingTemplate messagingTemplate;

    public DispatchController(SpringDataDispatchRepository repository, SimpMessagingTemplate messagingTemplate) {
        this.repository = repository;
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping
    public ResponseEntity<DispatchEntity> createDispatch(@RequestBody DispatchEntity dispatch) {
        dispatch.setTimestamp(java.time.LocalDateTime.now());
        dispatch.setStatut("EN_ATTENTE");
        DispatchEntity saved = repository.save(dispatch);
        
        // Notify via WebSocket
        messagingTemplate.convertAndSend("/topic/dispatch", saved);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/driver/{driverId}")
    public List<DispatchEntity> getDriverDispatch(@PathVariable Long driverId) {
        return repository.findByReceiverId(driverId);
    }

    @GetMapping("/sender/{senderId}")
    public List<DispatchEntity> getSenderDispatch(@PathVariable Long senderId) {
        return repository.findBySenderId(senderId);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DispatchEntity> updateStatus(@PathVariable Long id, @RequestParam String status) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setStatut(status);
                    DispatchEntity saved = repository.save(existing);
                    
                    // Notify via WebSocket
                    messagingTemplate.convertAndSend("/topic/dispatch", saved);
                    return ResponseEntity.ok(saved);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
