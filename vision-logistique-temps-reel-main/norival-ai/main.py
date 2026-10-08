import json
import time
import random
import re
from datetime import datetime
from kafka import KafkaProducer
import urllib.request
import urllib.error
import cv2
import easyocr
from ultralytics import YOLO
import collections
import threading
import requests
import os

def save_and_upload_video(frames, title, status, fps, width, height):
    import imageio
    filename = "anomaly.mp4"
    writer = imageio.get_writer(filename, fps=fps)
    for f in frames:
        # Convert BGR to RGB for imageio
        rgb_frame = cv2.cvtColor(f, cv2.COLOR_BGR2RGB)
        writer.append_data(rgb_frame)
    writer.close()
    try:
        url = "http://localhost:8080/api/videos/upload"
        with open(filename, 'rb') as f:
            files = {'file': (f"{title.replace(' ', '_')}.mp4", f, 'video/mp4')}
            data = {'title': title, 'status': status, 'duration': "00:06"}
            
            headers = {}
            global AUTH_TOKEN
            if AUTH_TOKEN:
                headers = {'Authorization': f"Bearer {AUTH_TOKEN}"}
                
            res = requests.post(url, files=files, data=data, headers=headers)
            if res.status_code == 200:
                print("Video upload SUCCESS")
            else:
                print(f"Upload video status: {res.status_code} - {res.text}")
    except Exception as e:
        print("Erreur upload video:", e)

# Configuration Kafka
KAFKA_TOPIC = 'norival-detections'
KAFKA_BOOTSTRAP_SERVERS = ['localhost:19092']

print("Initialisation du producteur Kafka...")
try:
    producer = KafkaProducer(
        bootstrap_servers=KAFKA_BOOTSTRAP_SERVERS,
        value_serializer=lambda v: json.dumps(v).encode('utf-8')
    )
    print("Connecté au broker Kafka.")
except Exception as e:
    print(f"Avertissement: Impossible de se connecter à Kafka ({e}). Les événements seront uniquement affichés en console.")
    producer = None

# Liste de véhicules et chantiers simulés
VEHICULES_SIMULES = []
CHANTIERS = []
AUTH_TOKEN = ""

API_BASE_URL = "http://localhost:8080/api"

def load_data_from_db():
    global VEHICULES_SIMULES, CHANTIERS, AUTH_TOKEN
    try:
        # 1. Login to get token
        login_data = json.dumps({"username": "admin", "password": "admin"}).encode('utf-8')
        req = urllib.request.Request(f"{API_BASE_URL}/auth/login", data=login_data, headers={'Content-Type': 'application/json'})
        with urllib.request.urlopen(req, timeout=3) as response:
            res = json.loads(response.read().decode())
            AUTH_TOKEN = res.get("token")
            print("Python IA: Authentifié auprès de Spring Boot. Jeton JWT obtenu.")
            
        headers = {'Authorization': f"Bearer {AUTH_TOKEN}"}
        
        # 2. Get vehicles
        req_veh = urllib.request.Request(f"{API_BASE_URL}/vehicules", headers=headers)
        with urllib.request.urlopen(req_veh, timeout=3) as response:
            veh_list = json.loads(response.read().decode())
            if veh_list:
                VEHICULES_SIMULES = [{"matricule": v["matricule"], "type": v["type"], "chantierId": v.get("chantierId")} for v in veh_list if v.get("type", "").lower() in ["camion", "engin"]]
                print(f"Python IA: {len(VEHICULES_SIMULES)} véhicules (camion/engin) chargés depuis la BDD.")
            else:
                VEHICULES_SIMULES = []
                print("Python IA: Aucun véhicule trouvé en BDD.")
                
        # 3. Get chantiers
        req_chan = urllib.request.Request(f"{API_BASE_URL}/chantiers", headers=headers)
        with urllib.request.urlopen(req_chan, timeout=3) as response:
            chan_list = json.loads(response.read().decode())
            if chan_list:
                CHANTIERS = [c["id"] for c in chan_list]
                print(f"Python IA: {len(CHANTIERS)} chantiers chargés depuis la BDD.")
            else:
                CHANTIERS = []
                print("Python IA: Aucun chantier trouvé en BDD.")
                
    except Exception as e:
        print(f"Erreur de rechargement des données de la BDD: {e}")

def send_kafka_event(matricule, type_engin, action, chantier_id):
    payload = {
        "matricule": matricule,
        "type": type_engin,
        "action": action,
        "chantierId": chantier_id,
        "timestamp": datetime.now().isoformat()
    }
    print(f"[KAFKA EVENT] {action} de {type_engin} ({matricule}) au Chantier {chantier_id}")
    if producer:
        try:
            producer.send(KAFKA_TOPIC, payload)
            producer.flush()
        except Exception as e:
            print(f"Erreur d'envoi Kafka: {e}")

print("Démarrage de la détection IA...")
load_data_from_db()

CAMERA_CHANTIER_ID = 4 # Identifiant logique de ce chantier physique

# Initialisation de YOLOv8
print("Chargement du modèle YOLOv8...")
model = YOLO('yolov8n.pt')

# Initialisation de EasyOCR
print("Chargement du modèle EasyOCR...")
reader = easyocr.Reader(['ar', 'en'], gpu=True) # Utilise le GPU si disponible

# Ouverture du flux vidéo (0 pour la webcam, ou chemin vers un fichier mp4)
video_source = 0 # Retour à la webcam
print("Tentative d'ouverture de la webcam...")
cap = cv2.VideoCapture(video_source, cv2.CAP_MSMF)

if not cap.isOpened():
    print("Échec avec MSMF, tentative standard...")
    cap = cv2.VideoCapture(video_source)

if not cap.isOpened():
    print(f"Erreur: Impossible d'ouvrir la source vidéo {video_source}")
    print("Veuillez brancher une webcam ou modifier 'video_source' avec le chemin d'un fichier mp4.")
    time.sleep(10)
    exit()

print("Webcam ouverte avec succès !")

# Paramètres de détection
frame_width = int(cap.get(cv2.CAP_PROP_FRAME_WIDTH))
frame_height = int(cap.get(cv2.CAP_PROP_FRAME_HEIGHT))
# Si la caméra ne retourne pas de dimension, on fixe une valeur par défaut
if frame_height == 0: frame_height = 480
if frame_width == 0: frame_width = 640

line_y = frame_height // 2
track_history = {}

# Variables pour l'enregistrement vidéo
frame_buffer = collections.deque(maxlen=30)
recording_frames = 0
post_anomaly_frames = []
is_recording = False
current_anomaly_status = ""
current_anomaly_title = ""

print("Appuyez sur 'q' dans la fenêtre vidéo pour quitter.")

while True:
    try:
        ret, frame = cap.read()
        if ret and video_source == 0:
            frame = cv2.flip(frame, 1) # Effet miroir pour la webcam
            
        if not ret:
            print("Fin de la vidéo ou erreur de lecture.")
            break
            
        # Recharger les données régulièrement si vides
        if not VEHICULES_SIMULES or not CHANTIERS:
            load_data_from_db()
            if not VEHICULES_SIMULES or not CHANTIERS:
                cv2.putText(frame, "En attente de connexion BDD...", (10, 30), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
                cv2.imshow('NORIVAL AI Detection', frame)
                if cv2.waitKey(1) & 0xFF == ord('q'):
                    break
                time.sleep(2)
                continue

        # Inférence YOLO avec tracking
        results = model.track(frame, persist=True, verbose=False)
        
        # Vérifier si des objets sont détectés
        if results[0].boxes is not None and results[0].boxes.id is not None:
            boxes = results[0].boxes.xyxy.cpu()
            track_ids = results[0].boxes.id.int().cpu().tolist()
            clss = results[0].boxes.cls.cpu().tolist()

            for box, track_id, cls in zip(boxes, track_ids, clss):
                if True:
                    x1, y1, x2, y2 = map(int, box)
                    cx = (x1 + x2) // 2
                    cy = (y1 + y2) // 2
                    
                    # Dessiner la boîte et le centre
                    cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)
                    cv2.circle(frame, (cx, cy), 5, (0, 0, 255), -1)

                    # Logique de franchissement de ligne
                    if track_id in track_history:
                        prev_cy = track_history[track_id]
                        trigger_event = False
                        action = ""
                        
                        if prev_cy < line_y and cy >= line_y:
                            action = "ENTREE"
                            trigger_event = True
                        elif prev_cy > line_y and cy <= line_y:
                            action = "SORTIE"
                            trigger_event = True
                            
                        if trigger_event and VEHICULES_SIMULES and CHANTIERS:
                            # Vrai OCR sur le bounding box
                            # On découpe l'image (attention aux bords de l'image)
                            crop_img = frame[max(0, y1):max(0, y2), max(0, x1):max(0, x2)]
                            
                            if crop_img.size > 0:
                                ocr_results = reader.readtext(crop_img, detail=0)
                                detected_text = "".join(ocr_results)
                                # Nettoyage: garder uniquement les caractères alphanumériques et arabes
                                cleaned_text = re.sub(r'[^a-zA-Z0-9\u0600-\u06FF]', '', detected_text).upper()
                                
                                print(f"[{action}] OCR détecté: '{detected_text}' -> nettoyé: '{cleaned_text}'")
                                
                                matched_vehicule = None
                                if cleaned_text and len(cleaned_text) >= 3:
                                    for v in VEHICULES_SIMULES:
                                        clean_matricule = re.sub(r'[^a-zA-Z0-9\u0600-\u06FF]', '', v['matricule']).upper()
                                        # Tolérance simple pour la démo: inclusion dans un sens ou dans l'autre
                                        if clean_matricule in cleaned_text or cleaned_text in clean_matricule:
                                            matched_vehicule = v
                                            break
                                            
                                if matched_vehicule:
                                    # Vérification stricte du chantier
                                    if matched_vehicule.get("chantierId") == CAMERA_CHANTIER_ID:
                                        status_text = f"AUTORISE: {matched_vehicule['matricule']}"
                                        color = (0, 255, 0) # Vert
                                    else:
                                        status_text = f"REFUSE (Mauvais Chantier): {matched_vehicule['matricule']}"
                                        color = (0, 0, 255) # Rouge
                                        if not is_recording:
                                            is_recording = True
                                            recording_frames = 30
                                            post_anomaly_frames = []
                                            current_anomaly_status = "Warning - " + status_text
                                            current_anomaly_title = "Camera - Anomalie Chantier"
                                        
                                    cv2.putText(frame, status_text, (x1, y1 - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.7, color, 2)
                                    send_kafka_event(matched_vehicule["matricule"], matched_vehicule["type"], action, CAMERA_CHANTIER_ID)
                                    print(status_text)
                                else:
                                    status_text = f"INCONNU: {cleaned_text}"
                                    cv2.putText(frame, status_text, (x1, y1 - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.7, (0, 0, 255), 2)
                                    print(status_text)
                                    if not is_recording:
                                        is_recording = True
                                        recording_frames = 30
                                        post_anomaly_frames = []
                                        current_anomaly_status = "Warning - " + status_text
                                        current_anomaly_title = "Camera - Véhicule Inconnu"
                            else:
                                print("Erreur de recadrage de l'image pour l'OCR")
                            
                    track_history[track_id] = cy
                    
        # Gestion du buffer et de l'enregistrement
        if not is_recording:
            frame_buffer.append(frame.copy())
        else:
            post_anomaly_frames.append(frame.copy())
            recording_frames -= 1
            if recording_frames <= 0:
                is_recording = False
                all_frames = list(frame_buffer) + post_anomaly_frames
                fps_val = cap.get(cv2.CAP_PROP_FPS)
                if fps_val == 0 or fps_val != fps_val: fps_val = 10.0
                t = threading.Thread(target=save_and_upload_video, args=(all_frames, current_anomaly_title, current_anomaly_status, fps_val, frame_width, frame_height))
                t.start()
                    
        # Dessiner la ligne de détection
        cv2.line(frame, (0, line_y), (frame_width, line_y), (255, 0, 0), 2)
        cv2.putText(frame, "Ligne de detection", (10, line_y - 10), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 0, 0), 1)

        # Affichage du rendu
        cv2.imshow('NORIVAL AI Detection', frame)

        # Quitter avec 'q'
        if cv2.waitKey(1) & 0xFF == ord('q'):
            break

    except KeyboardInterrupt:
        print("Arrêt de la détection.")
        break
    except Exception as e:
        print(f"Erreur dans la boucle de détection: {e}")
        time.sleep(1)

cap.release()
cv2.destroyAllWindows()
if producer:
    producer.close()
print("Processus IA arrêté.")
