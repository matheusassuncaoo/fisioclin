# 📘 Guia de Documentação da API - Swagger/OpenAPI

## 🚀 Acessando a Documentação

Após iniciar a aplicação, a documentação interativa da API estará disponível em:

### **Swagger UI (Interface Interativa)**
```
http://localhost:8080/swagger-ui.html
```

### **OpenAPI JSON (Especificação)**
```
http://localhost:8080/api-docs
```

## 📋 Recursos Disponíveis

### **1. Swagger UI**
- Interface visual e interativa
- Testar endpoints diretamente no navegador
- Ver schemas de request/response
- Documentação completa de parâmetros

### **2. Principais Endpoints Documentados**

#### **Pacientes**
- `GET /api/pacientes` - Listar todos os pacientes
- `GET /api/pacientes/ativos/com-nome` - Listar pacientes ativos com dados da pessoa
- `GET /api/pacientes/{id}/com-nome` - Buscar paciente específico com dados da pessoa
- `POST /api/pacientes` - Criar novo paciente
- `PUT /api/pacientes/{id}` - Atualizar paciente
- `PATCH /api/pacientes/{id}/inativar` - Inativar paciente

#### **Detalhes do Paciente**
- `GET /api/pacientes/{id}/detalhes` - Detalhes consolidados completos
- `POST /api/pacientes/evolucao-soap` - Criar evolução SOAP

## 🎯 Como Usar

### Testando um Endpoint

1. Acesse `http://localhost:8080/swagger-ui.html`
2. Expanda a tag desejada (ex: "Pacientes")
3. Clique no endpoint que deseja testar
4. Clique em **"Try it out"**
5. Preencha os parâmetros necessários
6. Clique em **"Execute"**
7. Veja a resposta abaixo

### Exemplo: Buscar Paciente por ID

```bash
# Via Swagger UI: Vá até "Pacientes" → GET /api/pacientes/{id}/com-nome
# Ou via cURL:

curl -X GET "http://localhost:8080/api/pacientes/1/com-nome" \
  -H "accept: application/json"
```

### Exemplo: Criar Evolução SOAP

```bash
curl -X POST "http://localhost:8080/api/pacientes/evolucao-soap" \
  -H "Content-Type: application/json" \
  -d '{
    "idPaciente": 1,
    "idProfissio": 1,
    "idEspec": 1,
    "dataAtendimento": "2025-11-21",
    "subjetivo": "Paciente relata dor reduzida",
    "objetivo": "Realizado exercícios de fortalecimento",
    "avaliacao": "Apresentou melhora de 30%",
    "plano": "Manter conduta atual"
  }'
```

## ⚙️ Configurações

As configurações do SpringDoc estão em `application.properties`:

```properties
# SpringDoc OpenAPI (Swagger)
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
springdoc.swagger-ui.tryItOutEnabled=true
```

## 🔒 Segurança

Atualmente a API está configurada para **permitir todas as requisições** (modo desenvolvimento).

Para produção, será necessário configurar autenticação/autorização no `SecurityConfig.java`.

## 📦 Schemas Disponíveis

A documentação inclui schemas detalhados para:

- **PacienteComNomeDTO** - Paciente com dados da pessoa física
- **PacienteDetalhesDTO** - Detalhes consolidados completos
- **AtendimentoSOAPDTO** - Evolução SOAP
- **Paciente** - Entidade base
- **Prontuario** - Prontuário médico

## 🌐 URLs por Ambiente

### Desenvolvimento
```
http://localhost:8080/swagger-ui.html
```

### Produção (quando configurado)
```
https://fisioclin.com.br/swagger-ui.html
```

## 📝 Notas

- A documentação é gerada automaticamente a partir das anotações no código
- Todos os endpoints REST são documentados automaticamente
- Os schemas são inferidos dos DTOs e Models
- Suporta validação de dados através das anotações `@Valid`

## 🆘 Problemas Comuns

### Swagger não carrega
- Verifique se a aplicação está rodando: `http://localhost:8080`
- Confirme que a dependência springdoc está no pom.xml
- Verifique os logs de inicialização

### Endpoint não aparece
- Certifique-se que o Controller tem `@RestController`
- Verifique se está no pacote correto (escaneamento de componentes)
- Recompile o projeto: `./mvnw clean compile`

---

**Desenvolvido com Spring Boot 3.5.6 + SpringDoc OpenAPI 2.6.0**
