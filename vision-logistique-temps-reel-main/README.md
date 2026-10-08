# NORIVAL - Système Distribué d'Optimisation Logistique Temps Réel

Ce projet est une application distribuée de niveau ingénieur combinant une architecture événementielle, de la vision par ordinateur et une recherche opérationnelle pour le pilotage multi-chantiers de la société NORIVAL.

## Architecture

Le projet est divisé en quatre composants principaux :
1. **Frontend (Angular 17+)** : Dashboard cartographique réactif (NgRx, Leaflet, WebSockets).
2. **Backend (Spring Boot 3.x)** : API REST, Clean Architecture, Moteur d'Optimisation (OptaPlanner), Sécurité (JWT).
3. **Intelligence Artificielle (Python)** : Pipeline de traitement vidéo simulé générant des événements Kafka.
4. **Infrastructure (Docker)** : Base de données PostgreSQL 18 et Broker de Messages Apache Kafka.

## Prérequis

- [Docker Desktop](https://www.docker.com/products/docker-desktop/) en cours d'exécution.
- [Node.js](https://nodejs.org/) (v18+ recommandé) et `npm`.
- [Java Development Kit (JDK)](https://adoptium.net/) version 17 ou 21.
- [Python 3.10+](https://www.python.org/downloads/).

## Guide d'Installation et Lancement

### Étape 1 : Démarrer l'infrastructure (Docker)
L'infrastructure contient PostgreSQL et Kafka. Depuis la racine du projet (`stage_4iir`) :
```bash
docker-compose up -d
```
Attendez que les conteneurs `norival-postgres` et `norival-kafka` soient lancés et sains.

### Étape 2 : Démarrer le Backend (Spring Boot)
Ouvrez un nouveau terminal et naviguez vers le dossier `norival-backend` :
```bash
cd norival-backend
./mvnw spring-boot:run
```
*Note Windows : Utilisez `mvnw.cmd spring-boot:run`.*
Le backend démarrera sur le port `8085`. Au premier lancement, un jeu de données de test (chantiers, véhicules, conducteurs) est automatiquement injecté dans la base PostgreSQL.

### Étape 3 : Démarrer le Frontend (Angular)
Ouvrez un nouveau terminal et naviguez vers le dossier `norival-frontend` :
```bash
cd norival-frontend
npm install
npm start
```
Le dashboard sera accessible à l'adresse [http://localhost:4200](http://localhost:4200).

### Étape 4 : Lancer le Simulateur IA (Python)
Pour simuler les détections de véhicules via caméras, naviguez vers le dossier `norival-ai` :
```bash
cd norival-ai
pip install -r requirements.txt
python main.py
```
Le script s'authentifiera auprès de l'API et commencera à envoyer des événements de rotation vers Kafka en temps réel.

## Identifiants de Test (Démo)
Une fois sur `http://localhost:4200`, vous pouvez vous connecter avec les profils suivants :
- **Superviseur Logistique** : `logistique@norival.com` (MDP : `pass123`)
- **Agent HSE** : `hse@norival.com` (MDP : `pass123`)
- **Chef de Chantier** : `chef1@norival.com` (MDP : `pass123`)

## Fonctionnalités Principales
- **Suivi Cartographique** : Visualisation en temps réel de la flotte sur une carte interactive.
- **Alertes Temps Réel** : Notifications instantanées (WebSockets) en cas d'accès non autorisé (Geofencing) ou incident HSE.
- **Recommandations d'Optimisation** : Aide à la décision pour la réaffectation des véhicules.
