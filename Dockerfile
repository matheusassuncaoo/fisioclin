# ===============================
# MULTI-STAGE BUILD
# ===============================

# Stage 1: Build da aplicação
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copia apenas o pom.xml primeiro (cache de dependências)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte
COPY src ./src

# Build da aplicação (pula testes para build mais rápido)
RUN mvn clean package -DskipTests -B

# ===============================
# Stage 2: Imagem de produção
# ===============================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Cria usuário não-root por segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia apenas o JAR compilado do stage anterior
COPY --from=builder /app/target/*.jar app.jar

# Variáveis de ambiente padrão
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Expõe a porta
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Comando para rodar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
