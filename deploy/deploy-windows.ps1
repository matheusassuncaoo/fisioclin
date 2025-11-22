# ===============================
# SCRIPT DEPLOY WINDOWS → LINUX
# ===============================
# Execute este script no WINDOWS para fazer deploy automático

# CONFIGURAÇÕES - AJUSTE AQUI!
$SERVIDOR = "usuario@ip-do-servidor"  # Ex: root@192.168.1.100
$PASTA_REMOTA = "/tmp"
$JAR_PATH = "target\fisioclin-0.0.1-SNAPSHOT.jar"

Write-Host "`n==========================================" -ForegroundColor Green
Write-Host "  Deploy Fisioclin para Servidor Linux" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

# 1. Build da aplicação
Write-Host "`n[1/4] Fazendo build da aplicação..." -ForegroundColor Yellow
.\mvnw.cmd clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Erro no build!" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Build concluído" -ForegroundColor Green

# 2. Verificar se JAR foi criado
if (-not (Test-Path $JAR_PATH)) {
    Write-Host "✗ JAR não encontrado em $JAR_PATH" -ForegroundColor Red
    exit 1
}
Write-Host "✓ JAR encontrado: $JAR_PATH" -ForegroundColor Green

# 3. Copiar JAR para servidor (via SCP)
Write-Host "`n[2/4] Copiando JAR para servidor..." -ForegroundColor Yellow
Write-Host "Servidor: $SERVIDOR" -ForegroundColor Cyan

scp $JAR_PATH "$SERVIDOR`:$PASTA_REMOTA/fisioclin-0.0.1-SNAPSHOT.jar"

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Erro ao copiar JAR!" -ForegroundColor Red
    Write-Host "`nVocê precisa ter SSH configurado." -ForegroundColor Yellow
    Write-Host "Instale OpenSSH: winget install Microsoft.OpenSSH.Beta" -ForegroundColor Yellow
    exit 1
}
Write-Host "✓ JAR copiado" -ForegroundColor Green

# 4. Copiar script de deploy
Write-Host "`n[3/4] Copiando script de deploy..." -ForegroundColor Yellow
scp deploy\deploy.sh "$SERVIDOR`:$PASTA_REMOTA/deploy.sh"

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Erro ao copiar script!" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Script copiado" -ForegroundColor Green

# 5. Executar deploy no servidor
Write-Host "`n[4/4] Executando deploy no servidor..." -ForegroundColor Yellow
Write-Host "ATENÇÃO: Você precisará ajustar as senhas no servidor!" -ForegroundColor Yellow

ssh $SERVIDOR "cd $PASTA_REMOTA && chmod +x deploy.sh && sudo ./deploy.sh"

Write-Host "`n==========================================" -ForegroundColor Green
Write-Host "  Deploy Concluído!" -ForegroundColor Green
Write-Host "==========================================" -ForegroundColor Green

Write-Host "`nPróximos passos no SERVIDOR:" -ForegroundColor Yellow
Write-Host "1. Editar senhas: sudo nano /etc/systemd/system/fisioclin.service" -ForegroundColor Cyan
Write-Host "2. Reiniciar: sudo systemctl restart fisioclin" -ForegroundColor Cyan
Write-Host "3. Ver logs: sudo journalctl -u fisioclin -f" -ForegroundColor Cyan
