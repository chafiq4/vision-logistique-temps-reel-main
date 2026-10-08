import json
import urllib.request
API_BASE_URL = "http://localhost:8080/api"
login_data = json.dumps({"username": "admin", "password": "admin"}).encode('utf-8')
req = urllib.request.Request(f"{API_BASE_URL}/auth/login", data=login_data, headers={'Content-Type': 'application/json'})
with urllib.request.urlopen(req) as response:
    res = json.loads(response.read().decode())
    token = res.get("token")

req_alerts = urllib.request.Request(f"{API_BASE_URL}/chantiers", headers={'Authorization': f"Bearer {token}"})
try:
    with urllib.request.urlopen(req_alerts) as response:
        alerts = json.loads(response.read().decode())
        print(json.dumps(alerts, indent=2))
except Exception as e:
    print(e)
