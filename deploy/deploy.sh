# ===============================
# SCRIPT DE DEPLOY SIMPLES
# ===============================
# Execute este script no SERVIDOR LINUX após copiar o JAR

#!/bin/bash

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}==================================${NC}"
echo -e "${GREEN}  Deploy Fisioclin - Produção${NC}"
echo -e "${GREEN}==================================${NC}"

# Variáveis - AJUSTE CONFORME SEU SERVIDOR
APP_NAME="fisioclin"
APP_DIR="/opt/fisioclin"
JAR_NAME="fisioclin-0.0.1-SNAPSHOT.jar"
APP_USER="fisioclin"
SERVICE_NAME="fisioclin.service"

# 1. Criar diretório da aplicação
echo -e "\n${YELLOW}[1/6] Criando diretório da aplicação...${NC}"
sudo mkdir -p $APP_DIR
sudo mkdir -p $APP_DIR/logs

# 2. Criar usuário para rodar a aplicação (segurança)
echo -e "${YELLOW}[2/6] Criando usuário da aplicação...${NC}"
if ! id "$APP_USER" &>/dev/null; then
    sudo useradd -r -s /bin/false $APP_USER
    echo -e "${GREEN}✓ Usuário criado${NC}"
else
    echo -e "${GREEN}✓ Usuário já existe${NC}"
fi

# 3. Parar serviço anterior (se existir)
echo -e "${YELLOW}[3/6] Parando serviço anterior...${NC}"
sudo systemctl stop $SERVICE_NAME 2>/dev/null || true

# 4. Copiar JAR (assumindo que está no diretório atual)
echo -e "${YELLOW}[4/6] Copiando JAR...${NC}"
if [ -f "$JAR_NAME" ]; then
    sudo cp $JAR_NAME $APP_DIR/app.jar
    echo -e "${GREEN}✓ JAR copiado${NC}"
else
    echo -e "${RED}✗ Erro: $JAR_NAME não encontrado!${NC}"
    echo -e "${YELLOW}Copie o JAR para este diretório primeiro.${NC}"
    exit 1
fi

# 5. Ajustar permissões
echo -e "${YELLOW}[5/6] Ajustando permissões...${NC}"
sudo chown -R $APP_USER:$APP_USER $APP_DIR
sudo chmod 500 $APP_DIR/app.jar

# 6. Criar/Atualizar serviço systemd
echo -e "${YELLOW}[6/6] Configurando serviço systemd...${NC}"
sudo tee /etc/systemd/system/$SERVICE_NAME > /dev/null <<EOF
[Unit]
Description=Fisioclin Spring Boot Application
After=syslog.target network.target

[Service]
User=$APP_USER
Group=$APP_USER
Type=simple

WorkingDirectory=$APP_DIR
ExecStart=/usr/bin/java -jar $APP_DIR/app.jar \\
    --spring.profiles.active=prod

# Variáveis de ambiente - AJUSTE AQUI!
Environment="DB_URL=jdbc:mysql://localhost:3306/fasiclin_db"
Environment="DB_USERNAME=fisioclin"
Environment="DB_PASSWORD=SUA_SENHA_AQUI"
Environment="ADMIN_USERNAME=admin"
Environment="ADMIN_PASSWORD=SUA_SENHA_ADMIN_AQUI"

# Logs
StandardOutput=append:$APP_DIR/logs/app.log
StandardError=append:$APP_DIR/logs/error.log

# Restart em caso de falha
Restart=always
RestartSec=10

# Limites de recursos
LimitNOFILE=65536

[Install]
WantedBy=multi-user.target
EOF

# Recarregar systemd
sudo systemctl daemon-reload

# Habilitar inicialização automática
sudo systemctl enable $SERVICE_NAME

# Iniciar serviço
echo -e "\n${GREEN}Iniciando serviço...${NC}"
sudo systemctl start $SERVICE_NAME

# Aguardar um pouco
sleep 3

# Verificar status
echo -e "\n${GREEN}==================================${NC}"
echo -e "${GREEN}  Status do Serviço${NC}"
echo -e "${GREEN}==================================${NC}"
sudo systemctl status $SERVICE_NAME --no-pager

echo -e "\n${GREEN}==================================${NC}"
echo -e "${GREEN}  Deploy Concluído!${NC}"
echo -e "${GREEN}==================================${NC}"
echo -e "\n${YELLOW}Comandos úteis:${NC}"
echo -e "  Ver logs:      sudo journalctl -u $SERVICE_NAME -f"
echo -e "  Status:        sudo systemctl status $SERVICE_NAME"
echo -e "  Parar:         sudo systemctl stop $SERVICE_NAME"
echo -e "  Reiniciar:     sudo systemctl restart $SERVICE_NAME"
echo -e "  Ver logs app:  sudo tail -f $APP_DIR/logs/app.log"
