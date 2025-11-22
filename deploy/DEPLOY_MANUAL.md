# 🚀 Deploy Manual Simples (SEM Docker)

## 📋 Pré-requisitos no Servidor Linux

```bash
# 1. Java 21 ou superior
sudo apt update
sudo apt install openjdk-21-jre-headless -y
java -version

# 2. MySQL Server
sudo apt install mysql-server -y
sudo systemctl start mysql
sudo systemctl enable mysql

# 3. Criar banco de dados
sudo mysql
```

```sql
CREATE DATABASE fasiclin_db;
CREATE USER 'fisioclin'@'localhost' IDENTIFIED BY 'sua_senha_aqui';
GRANT ALL PRIVILEGES ON fasiclin_db.* TO 'fisioclin'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

---

## 🎯 Deploy - Opção 1: Manual Passo a Passo

### **No Windows (sua máquina):**

```powershell
# 1. Build do projeto
.\mvnw.cmd clean package -DskipTests

# 2. JAR estará em: target\fisioclin-0.0.1-SNAPSHOT.jar
```

### **Transferir para Servidor:**

**Opção A - Usando WinSCP (mais fácil):**
1. Baixe WinSCP: https://winscp.net/
2. Conecte no servidor
3. Copie `target\fisioclin-0.0.1-SNAPSHOT.jar` para `/tmp/`
4. Copie `deploy\deploy.sh` para `/tmp/`

**Opção B - Usando SCP (linha de comando):**
```powershell
scp target\fisioclin-0.0.1-SNAPSHOT.jar usuario@ip-servidor:/tmp/
scp deploy\deploy.sh usuario@ip-servidor:/tmp/
```

### **No Servidor Linux:**

```bash
# 1. Ir para pasta temporária
cd /tmp

# 2. Dar permissão de execução
chmod +x deploy.sh

# 3. Executar deploy
sudo ./deploy.sh

# 4. IMPORTANTE: Editar senhas
sudo nano /etc/systemd/system/fisioclin.service

# Encontre estas linhas e ajuste:
# Environment="DB_PASSWORD=SUA_SENHA_AQUI"
# Environment="ADMIN_PASSWORD=SUA_SENHA_ADMIN_AQUI"

# Salvar: Ctrl+O, Enter, Ctrl+X

# 5. Recarregar e reiniciar
sudo systemctl daemon-reload
sudo systemctl restart fisioclin

# 6. Verificar se está rodando
sudo systemctl status fisioclin
```

---

## 🎯 Deploy - Opção 2: Script Automático (Avançado)

Se você tiver SSH configurado no Windows:

```powershell
# Edite o arquivo deploy\deploy-windows.ps1 primeiro
# Ajuste a linha: $SERVIDOR = "usuario@ip-do-servidor"

# Execute:
.\deploy\deploy-windows.ps1
```

---

## 📊 Gerenciar Aplicação no Servidor

### **Ver Status**
```bash
sudo systemctl status fisioclin
```

### **Ver Logs em Tempo Real**
```bash
# Logs do sistema
sudo journalctl -u fisioclin -f

# Logs da aplicação
sudo tail -f /opt/fisioclin/logs/app.log

# Logs de erro
sudo tail -f /opt/fisioclin/logs/error.log
```

### **Controlar Serviço**
```bash
# Parar
sudo systemctl stop fisioclin

# Iniciar
sudo systemctl start fisioclin

# Reiniciar
sudo systemctl restart fisioclin

# Ver se está habilitado para iniciar com sistema
sudo systemctl is-enabled fisioclin
```

### **Desabilitar Inicialização Automática**
```bash
sudo systemctl disable fisioclin
```

---

## 🔄 Atualizar Aplicação (Novo Deploy)

### **Processo rápido:**

```powershell
# No Windows - Build
.\mvnw.cmd clean package -DskipTests

# Copiar novo JAR
scp target\fisioclin-0.0.1-SNAPSHOT.jar usuario@servidor:/tmp/
```

```bash
# No Servidor - Parar, substituir, iniciar
sudo systemctl stop fisioclin
sudo cp /tmp/fisioclin-0.0.1-SNAPSHOT.jar /opt/fisioclin/app.jar
sudo chown fisioclin:fisioclin /opt/fisioclin/app.jar
sudo systemctl start fisioclin
sudo systemctl status fisioclin
```

---

## 🐛 Troubleshooting

### **Aplicação não inicia:**
```bash
# Ver logs detalhados
sudo journalctl -u fisioclin -n 100 --no-pager

# Testar JAR manualmente
cd /opt/fisioclin
sudo -u fisioclin java -jar app.jar --spring.profiles.active=prod
```

### **Erro de conexão com banco:**
```bash
# Verificar se MySQL está rodando
sudo systemctl status mysql

# Testar conexão
mysql -u fisioclin -p -h localhost fasiclin_db
```

### **Aplicação muito lenta:**
```bash
# Editar serviço e adicionar mais memória
sudo nano /etc/systemd/system/fisioclin.service

# Trocar linha ExecStart por:
# ExecStart=/usr/bin/java -Xmx1024m -Xms512m -jar /opt/fisioclin/app.jar --spring.profiles.active=prod

# Reiniciar
sudo systemctl daemon-reload
sudo systemctl restart fisioclin
```

---

## 🔐 Segurança Básica

### **Firewall - Permitir apenas porta 8080:**
```bash
sudo ufw allow 22/tcp      # SSH
sudo ufw allow 8080/tcp    # Aplicação
sudo ufw enable
sudo ufw status
```

### **NGINX como Proxy Reverso (Opcional):**
```bash
# Instalar NGINX
sudo apt install nginx -y

# Criar configuração
sudo nano /etc/nginx/sites-available/fisioclin
```

```nginx
server {
    listen 80;
    server_name seu-dominio.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

```bash
# Ativar site
sudo ln -s /etc/nginx/sites-available/fisioclin /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl restart nginx
```

---

## 📝 Checklist de Deploy

- [ ] Java instalado no servidor
- [ ] MySQL instalado e rodando
- [ ] Banco de dados criado
- [ ] Usuário do banco criado
- [ ] JAR compilado
- [ ] JAR copiado para servidor
- [ ] Script deploy.sh executado
- [ ] Senhas ajustadas no service
- [ ] Serviço iniciado
- [ ] Logs verificados
- [ ] Aplicação acessível via navegador

---

## 🎓 Fluxo Resumido

```
Windows:
  ├─ Desenvolve código
  ├─ .\mvnw.cmd clean package
  └─ Gera JAR em target/

Transferência:
  └─ SCP ou WinSCP → Servidor Linux

Servidor Linux:
  ├─ ./deploy.sh
  ├─ Ajusta senhas
  ├─ systemctl start fisioclin
  └─ Aplicação rodando!
```

---

## 🆘 Comandos de Emergência

```bash
# Parar tudo
sudo systemctl stop fisioclin

# Ver últimos erros
sudo journalctl -u fisioclin -n 50 --no-pager

# Remover serviço
sudo systemctl stop fisioclin
sudo systemctl disable fisioclin
sudo rm /etc/systemd/system/fisioclin.service
sudo systemctl daemon-reload
```
