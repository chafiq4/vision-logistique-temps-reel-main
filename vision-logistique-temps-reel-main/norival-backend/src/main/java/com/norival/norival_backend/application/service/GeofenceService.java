package com.norival.norival_backend.application.service;

import com.norival.norival_backend.domain.model.Vehicule;
import com.norival.norival_backend.infrastructure.persistence.entity.AlerteEntity;
import com.norival.norival_backend.infrastructure.persistence.repository.SpringDataAlerteRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GeofenceService {

    private final SpringDataAlerteRepository alerteRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // Map of City Name -> [Latitude, Longitude]
    private final Map<String, double[]> villes = new HashMap<>();

    // Map to prevent spam: "VehiculeId_CityName" -> Last Alert Time
    private final Map<String, LocalDateTime> alertHistory = new ConcurrentHashMap<>();

    private static final double RADIUS_KM = 5.0; // 5 km radius

    public GeofenceService(SpringDataAlerteRepository alerteRepository, SimpMessagingTemplate messagingTemplate) {
        this.alerteRepository = alerteRepository;
        this.messagingTemplate = messagingTemplate;

        // Populate predefined cities
        villes.put("Casablanca", new double[]{33.5731, -7.5898});
        villes.put("Rabat", new double[]{34.0209, -6.8416});
        villes.put("Marrakech", new double[]{31.6295, -7.9811});
        villes.put("Tanger", new double[]{35.7595, -5.8340});
        villes.put("Agadir", new double[]{30.4278, -9.5981});
        villes.put("Fès", new double[]{34.0331, -5.0003});
    }

    public void checkPosition(Vehicule vehicule) {
        if (vehicule.getLatitude() == null || vehicule.getLongitude() == null) return;

        for (Map.Entry<String, double[]> entry : villes.entrySet()) {
            String villeNom = entry.getKey();
            double[] coords = entry.getValue();

            double distance = haversine(vehicule.getLatitude(), vehicule.getLongitude(), coords[0], coords[1]);

            if (distance <= RADIUS_KM) {
                String cacheKey = vehicule.getId() + "_" + villeNom;
                LocalDateTime lastAlert = alertHistory.get(cacheKey);

                // Alert only if we haven't alerted in the last 60 minutes
                if (lastAlert == null || lastAlert.plusMinutes(60).isBefore(LocalDateTime.now())) {
                    triggerCityAlert(vehicule, villeNom);
                    alertHistory.put(cacheKey, LocalDateTime.now());
                }
            }
        }
    }

    private void triggerCityAlert(Vehicule vehicule, String villeNom) {
        String message = "Le camion " + vehicule.getMatricule() + " traverse la zone urbaine de : " + villeNom;

        AlerteEntity alerte = new AlerteEntity(
                null,
                "LOGISTIQUE",
                message,
                "ACTIVE",
                LocalDateTime.now(),
                vehicule.getChantierId(),
                vehicule.getMatricule(),
                "SURVEILLER"
        );
        alerteRepository.save(alerte);

        // Broadcast websocket
        Map<String, Object> event = new HashMap<>();
        event.put("id", alerte.getId());
        event.put("type", alerte.getType());
        event.put("message", message);
        event.put("statut", alerte.getStatut());
        event.put("timestamp", alerte.getTimestamp().toString());
        event.put("criticite", alerte.getCriticite());

        messagingTemplate.convertAndSend("/topic/alerts", event);
    }

    // Haversine formula to calculate distance between two points on Earth in km
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
