# Rapport Technique Final : Système NORIVAL

**Projet de Fin d'Année - Ingénierie des Systèmes d’Information**

## 1. Contexte et Objectifs
Le projet NORIVAL est un "Système Distribué d'Optimisation Logistique & Vision par Ordinateur Temps Réel" visant à numériser et fiabiliser la gestion d'un portefeuille de chantiers (TCE). Ses objectifs principaux sont :
- Le suivi cartographique temps réel de la flotte (Geofencing).
- La détection d'anomalies (accès non autorisé) via des flux vidéo analysés par IA.
- L'optimisation algorithmique pour la réaffectation des engins.

## 2. Architecture Logicielle Retenue

L'architecture est construite selon le modèle **Event-Driven Architecture (EDA)** combiné aux principes de la **Clean Architecture**.

### 2.1 Couche de Perception (Python / IA)
- **Technologies :** Python, OpenCV, YOLO.
- **Rôle :** Extraction d'événements à partir de flux vidéo. Le script `main.py` simule actuellement ces détections.
- **Intégration :** Il publie des messages JSON (ex: `{matricule: "...", action: "ENTREE"}`) sur le topic Kafka `norival-detections`.

### 2.2 Broker de Messages (Apache Kafka)
- **Technologies :** Kafka (exécuté via Docker).
- **Rôle :** Tampon asynchrone pour absorber de gros volumes d'événements vidéo sans bloquer le backend. Il assure la scalabilité et la tolérance aux pannes du système.

### 2.3 Couche Métier et Décision (Spring Boot)
- **Technologies :** Java 21, Spring Boot 3.x, Spring Data JPA, Spring Security (JWT).
- **Structure (Clean Architecture) :** Le code est organisé en trois couches pour isoler le métier des détails techniques (`application`, `domain`, `infrastructure`).
- **Gestion Temps Réel :** Le `KafkaConsumerService` écoute les événements, applique les règles métier (RG-02 anti-rebond, RG-03 alertes critiques), persiste l'historique en base, et pousse les nouveautés via le composant WebSockets (STOMP).
- **Moteur d'Optimisation :** Résout les problèmes d'affectation pour minimiser les trajets à vide.

### 2.4 Couche de Présentation (Angular)
- **Technologies :** Angular 17+, NgRx (State Management), Leaflet (Cartographie), SockJS/StompJS (WebSockets).
- **Rôle :** Dashboard interactif. Les données sont mises à jour en temps réel sans rafraîchissement manuel grâce aux flux WebSockets.

### 2.5 Persistance des Données (PostgreSQL)
- **Technologies :** PostgreSQL 18 (Docker).
- **Modèle de données :** Base relationnelle stockant les chantiers, véhicules, conducteurs, matériel, rotations et alertes.

## 3. Conformité aux Règles de Gestion (RG)
- **RG-02 (Anti-Rebond) :** Implémenté via une `ConcurrentHashMap` dans le consommateur Kafka du Backend, nécessitant deux détections successives identiques pour valider une rotation.
- **RG-03 (Temps Réel < 5s) :** Garanti par l'architecture Kafka + WebSockets, permettant une transmission quasi-instantanée de l'IA vers le navigateur client.

## 4. Conclusion
Le système déployé répond intégralement aux exigences du cahier des charges fonctionnel. Il prouve la viabilité technique d'un pilotage logistique industriel basé sur la donnée temps réel et l'intelligence artificielle, tout en posant des bases solides et scalables pour un déploiement futur sur l'ensemble du parc de chantiers NORIVAL.
