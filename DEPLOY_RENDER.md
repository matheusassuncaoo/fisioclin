# Deploy FisioClin no Render

## 🚀 Passos para Deploy

### 1. Criar conta no Render
- Acesse [render.com](https://render.com)
- Crie conta ou faça login com GitHub

### 2. Conectar repositório
- Faça push do código para GitHub
- No Render, clique em **New → Web Service**
- Conecte seu repositório GitHub

### 3. Configurar o serviço

| Campo | Valor |
|-------|-------|
| **Name** | fisioclin |
| **Region** | Oregon (ou Frankfurt) |
| **Branch** | main |
| **Root Directory** | (deixe vazio) |
| **Runtime** | Docker |
| **Dockerfile Path** | ./Dockerfile |
| **Plan** | Free |

### 4. Variáveis de Ambiente

Adicione no painel do Render (Settings → Environment):

```
SPRING_PROFILES_ACTIVE=prod
DB_URL=jdbc:mysql://160.20.22.99:3360/fasiclin
DB_USERNAME=aluno4
DB_PASSWORD=OaAQLL+YVCU=
CORS_ALLOWED_ORIGINS=https://fisioclin.onrender.com,http://localhost:5500
JAVA_OPTS=-Xmx256m -Xms128m
ADMIN_USERNAME=admin
ADMIN_PASSWORD=(gere uma senha segura)
```

### 5. Deploy
- Clique em **Create Web Service**
- Aguarde o build (5-10 minutos)
- Acesse: `https://fisioclin.onrender.com/frontend/index.html`

## ⚠️ Importante

### Free Tier do Render
- Serviço "dorme" após 15min de inatividade
- Primeiro acesso após dormir demora ~30s
- Limite de 750h/mês de uso

### Banco de Dados
- O banco Fasitech está externo (IP: 160.20.22.99:3360)
- Verifique se o Render consegue conectar (pode ter firewall)

## 🔧 Troubleshooting

### Erro de conexão com banco
```
CORS_ALLOWED_ORIGINS deve incluir a URL do seu app
```

### Erro de memória
```
Aumente JAVA_OPTS ou use plano Starter
```

### Frontend não carrega
```
Acesse: https://fisioclin.onrender.com/frontend/index.html
(não apenas a raiz /)
```

## 📱 URLs do Sistema

| Ambiente | URL |
|----------|-----|
| **Frontend** | https://fisioclin.onrender.com/frontend/index.html |
| **API** | https://fisioclin.onrender.com/api/pacientes |
| **Health Check** | https://fisioclin.onrender.com/api/pacientes |

