#!/bin/bash
set -e

echo "========================================="
echo " 999x Game Backend - EC2 Deployment Script"
echo "========================================="

# 1. Check Java 17
if ! command -v java &> /dev/null; then
    echo "Installing Java 17..."
    sudo apt update
    sudo apt install -y openjdk-17-jre-headless
fi

# 2. Make gradlew executable & Build Fat JAR
echo "Building backend production Fat JAR..."
chmod +x ./gradlew
./gradlew :backend:buildFatJar --no-daemon

# 3. Create Systemd Service for Production
echo "Setting up systemd service (ingames-backend)..."

cat <<EOF | sudo tee /etc/systemd/system/ingames-backend.service > /dev/null
[Unit]
Description=999x Game Ktor Backend Service
After=network.target

[Service]
User=$USER
WorkingDirectory=$PWD
ExecStart=/usr/bin/java -jar $PWD/backend/build/libs/backend-all.jar
Restart=always
RestartSec=10
EnvironmentFile=$PWD/.env
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

# 4. Enable & Restart Service
sudo systemctl daemon-reload
sudo systemctl enable ingames-backend
sudo systemctl restart ingames-backend

echo "========================================="
echo "Deployment Completed Successfully!"
echo "Check service status with: sudo systemctl status ingames-backend"
echo "View live logs with: sudo journalctl -u ingames-backend -f"
echo "========================================="
