import urllib.request
import json
data = json.dumps({"username":"chef1@norival.com", "password":"pass123"}).encode('utf-8')
req = urllib.request.Request('http://localhost:8080/api/auth/login', data=data, headers={'Content-Type': 'application/json'})
print(urllib.request.urlopen(req).read().decode())
