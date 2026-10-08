Write-Host "Lancement des services Docker (Postgres, Kafka)..." -ForegroundColor Cyan
docker-compose up -d

Write-Host "Lancement du Backend (Spring Boot)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-Command", "cd norival-backend; .\mvnw.cmd spring-boot:run"

Write-Host "Lancement du Frontend (Angular)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-Command", "cd norival-frontend; npm start"

Write-Host "Lancement de l'IA (Python)..." -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-ExecutionPolicy", "Bypass", "-Command", "cd norival-ai; python main.py"

Write-Host "Le projet est en cours de lancement dans des fenetres separees !" -ForegroundColor Green
