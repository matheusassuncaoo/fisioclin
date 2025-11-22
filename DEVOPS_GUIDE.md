# 🚀 Guia DevOps - Fisioclin

## 📋 Estrutura de Ambientes

Agora o sistema está preparado para 3 ambientes:

### 1️⃣ **Desenvolvimento (dev)**
- Roda localmente na sua máquina
- Logs detalhados
- Swagger habilitado
- Banco MySQL local

### 2️⃣ **Homologação (homolog)**
- Ambiente de testes antes de produção
- Logs moderados
- Validação de features

### 3️⃣ **Produção (prod)**
- Ambiente real dos usuários
- Logs apenas de erros
- Swagger desabilitado
- Máxima performance e segurança

---

## 🎯 Como Usar Cada Ambiente

### **Desenvolvimento Local (Modo Tradicional)**
```bash
# Rodar com profile dev (padrão)
.\mvnw.cmd spring-boot:run

# Ou especificar explicitamente
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev
```

### **Desenvolvimento com Docker**
```bash
# Subir aplicação + MySQL automaticamente
docker-compose up -d

# Ver logs
docker-compose logs -f app

# Parar tudo
docker-compose down
```

### **Homologação**
```bash
# Definir variáveis de ambiente
$env:SPRING_PROFILES_ACTIVE="homolog"
$env:DB_URL="jdbc:mysql://servidor-homolog:3306/fisioclin"
$env:DB_USERNAME="usuario_homolog"
$env:DB_PASSWORD="senha_homolog"
$env:ADMIN_PASSWORD="senha_admin_homolog"

# Rodar
.\mvnw.cmd spring-boot:run
```

### **Produção**
```bash
# Build do JAR
.\mvnw.cmd clean package -DskipTests

# Rodar com variáveis de ambiente de produção
java -jar target/fisioclin-0.0.1-SNAPSHOT.jar `
  --spring.profiles.active=prod `
  -Dserver.port=8080
```

---

## 🐳 Docker - Comandos Úteis

### **Build da Imagem**
```bash
docker build -t fisioclin:latest .
```

### **Rodar Container Individual**
```bash
docker run -d \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL="jdbc:mysql://mysql:3306/fasiclin_db" \
  -e DB_USERNAME=fisioclin \
  -e DB_PASSWORD=senha123 \
  -e ADMIN_USERNAME=admin \
  -e ADMIN_PASSWORD=senhaSegura \
  --name fisioclin-app \
  fisioclin:latest
```

### **Docker Compose - Comandos**
```bash
# Subir tudo em background
docker-compose up -d

# Ver logs em tempo real
docker-compose logs -f

# Parar serviços
docker-compose stop

# Parar e remover containers
docker-compose down

# Rebuild e subir
docker-compose up --build -d

# Ver status
docker-compose ps
```

---

## 🔧 GitHub Actions - CI/CD Automático

O pipeline roda automaticamente quando você faz push:

### **Etapas do Pipeline**

1. **Build e Testes** (sempre)
   - Compila o código
   - Roda os testes
   - Gera o JAR
   - Salva artefato por 5 dias

2. **Build Docker** (apenas branch main)
   - Cria imagem Docker
   - Usa cache para velocidade

3. **Dependency Graph** (apenas branch main)
   - Atualiza grafo de dependências
   - Melhora alertas de segurança

### **Ver Resultados**
- Acesse: `GitHub → Actions → CI/CD Pipeline`
- Veja logs de cada job
- Baixe o JAR gerado

---

## 🔐 Variáveis de Ambiente Importantes

### **Desenvolvimento**
Não precisa definir nada, usa valores padrão.

### **Homologação/Produção**
```bash
# Obrigatórias
SPRING_PROFILES_ACTIVE=homolog  # ou prod
DB_URL=jdbc:mysql://...
DB_USERNAME=usuario
DB_PASSWORD=senha
ADMIN_PASSWORD=senha_admin

# Opcionais
ADMIN_USERNAME=admin           # padrão: admin
PORT=8080                      # padrão: 8080
APP_URL=https://seu-site.com   # para Swagger
```

---

## 📊 Diferenças Entre Ambientes

| Recurso | Dev | Homolog | Prod |
|---------|-----|---------|------|
| **Logs SQL** | ✅ Sim | ❌ Não | ❌ Não |
| **Swagger** | ✅ Sim | ✅ Sim | ❌ Não |
| **Pool Conexões** | 3 | 10 | 20 |
| **Detalhes de Erro** | ✅ Completos | ⚠️ Moderados | ❌ Ocultos |
| **Compressão** | ❌ Não | ✅ Sim | ✅ Sim |

---

## 🚀 Deploy Passo a Passo

### **Opção 1: Deploy Manual com JAR**
```bash
# 1. Build
.\mvnw.cmd clean package -DskipTests

# 2. Copiar JAR para servidor
scp target/*.jar usuario@servidor:/app/

# 3. No servidor, rodar
java -jar /app/fisioclin-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=prod
```

### **Opção 2: Deploy com Docker**
```bash
# 1. Build imagem
docker build -t fisioclin:v1.0 .

# 2. Salvar imagem
docker save fisioclin:v1.0 > fisioclin.tar

# 3. Copiar para servidor
scp fisioclin.tar usuario@servidor:/tmp/

# 4. No servidor
docker load < /tmp/fisioclin.tar
docker run -d -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  ... (variáveis de ambiente) \
  fisioclin:v1.0
```

---

## 🛠️ Troubleshooting

### **Erro: "Could not connect to database"**
```bash
# Verificar se MySQL está rodando
docker-compose ps

# Ver logs do MySQL
docker-compose logs mysql

# Testar conexão
docker exec -it fisioclin-mysql mysql -u root -p
```

### **Erro: "Port 8080 already in use"**
```bash
# Windows - Encontrar processo na porta
netstat -ano | findstr :8080

# Matar processo
Stop-Process -Id <PID> -Force

# Ou mudar porta
$env:SERVER_PORT=8081
```

### **Ver logs da aplicação**
```bash
# Docker Compose
docker-compose logs -f app

# Container individual
docker logs -f fisioclin-app
```

---

## 📝 Boas Práticas Implementadas

✅ **Separação de ambientes** (dev/homolog/prod)  
✅ **Profiles do Spring Boot**  
✅ **Variáveis de ambiente para segredos**  
✅ **Docker multi-stage build** (imagem otimizada)  
✅ **Health checks** nos containers  
✅ **CI/CD com GitHub Actions**  
✅ **Cache de dependências Maven**  
✅ **Usuário não-root no Docker**  
✅ **Compressão HTTP em produção**  
✅ **Logs apropriados por ambiente**  

---

## 🎓 Próximos Passos (Opcional)

Se quiser evoluir mais, pode adicionar:

- **Monitoramento**: Prometheus + Grafana
- **Logs centralizados**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **Secrets management**: HashiCorp Vault ou AWS Secrets Manager
- **Deploy automático**: ArgoCD, Jenkins, ou GitHub Actions com CD
- **Testes automatizados**: JUnit, TestContainers
- **Análise de código**: SonarQube
- **Backup automático**: Scripts de backup do banco

---

## 📞 Comandos Rápidos

```bash
# Desenvolvimento rápido
docker-compose up -d

# Build para produção
.\mvnw.cmd clean package -DskipTests

# Rodar testes
.\mvnw.cmd test

# Limpar tudo
docker-compose down -v
.\mvnw.cmd clean
```
