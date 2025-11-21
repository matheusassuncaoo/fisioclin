# 🚀 Guia para Corrigir o Banco de Dados - Fisioclin

## 🔍 Problema Identificado

A tabela `PACIENTE` no banco de dados **não possui a coluna `ID_PESSOAFIS`**, que é necessária para estabelecer a relação com a tabela `PESSOAFIS`.

## ⚡ Solução Rápida

### Opção 1: Recriar o banco do zero (RECOMENDADO se for ambiente de desenvolvimento)

```powershell
# 1. Parar a aplicação Spring Boot (se estiver rodando)
# 2. Conectar ao MySQL
mysql -u root -p

# 3. Dropar e recriar o banco
DROP DATABASE IF EXISTS fasiclin_db;
CREATE DATABASE fasiclin_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE fasiclin_db;

# 4. Executar o script completo
SOURCE "c:/Users/Matheus/Documents/Projetos de Programação/fisioclin/fasiclin_db(1).sql";

# 5. Sair do MySQL
EXIT;
```

### Opção 2: Adicionar apenas a coluna faltante (se houver dados importantes)

```sql
USE fasiclin_db;

-- Adicionar a coluna ID_PESSOAFIS
ALTER TABLE PACIENTE 
ADD COLUMN ID_PESSOAFIS INT AFTER IDPACIENTE;

-- Adicionar constraint UNIQUE
ALTER TABLE PACIENTE 
ADD CONSTRAINT UC_PACIENTE_PESSOAFIS UNIQUE (ID_PESSOAFIS);

-- Adicionar chave estrangeira
ALTER TABLE PACIENTE 
ADD CONSTRAINT FK_PACIENTE_PESSOAFIS 
FOREIGN KEY(ID_PESSOAFIS) REFERENCES PESSOAFIS(IDPESSOAFIS);

-- Se já houver pacientes cadastrados, você precisará popular este campo manualmente
-- antes de adicionar a constraint NOT NULL:
-- UPDATE PACIENTE SET ID_PESSOAFIS = ? WHERE IDPACIENTE = ?;

-- Depois de popular, adicione NOT NULL:
ALTER TABLE PACIENTE 
MODIFY COLUMN ID_PESSOAFIS INT NOT NULL;
```

## ✅ Passos para Validar a Correção

1. **Verificar a estrutura da tabela:**
   ```sql
   USE fasiclin_db;
   DESCRIBE PACIENTE;
   ```
   
   Você deve ver a coluna `ID_PESSOAFIS` listada.

2. **Reiniciar a aplicação Spring Boot:**
   ```powershell
   cd 'c:\Users\Matheus\Documents\Projetos de Programação\fisioclin'
   .\mvnw.cmd spring-boot:run
   ```

3. **Verificar os logs** - não deve mais aparecer o erro:
   ```
   Unknown column 'p1_0.ID_PESSOAFIS' in 'field list'
   ```

4. **Testar o endpoint no navegador:**
   ```
   http://localhost:8080/api/pacientes/ativos
   ```

## 📋 Checklist de Verificação

- [ ] Banco de dados `fasiclin_db` existe
- [ ] Tabela `PACIENTE` possui a coluna `ID_PESSOAFIS`
- [ ] Constraint `FK_PACIENTE_PESSOAFIS` está criada
- [ ] Aplicação Spring Boot sobe sem erros
- [ ] Endpoint `/api/pacientes/ativos` retorna dados ou array vazio (sem erro 500)
- [ ] Frontend consegue fazer requisições sem `ERR_CONNECTION_REFUSED`

## 🎓 Aprendizado DevOps

Este tipo de problema é comum em times de desenvolvimento e demonstra a importância de:

1. **Versionamento de Schema (Migrations)**:
   - Usar ferramentas como Flyway ou Liquibase para versionar mudanças no banco
   - Cada alteração vira um arquivo de migração com timestamp
   - Garante que dev, staging e produção tenham o mesmo schema

2. **Validação de Schema no CI/CD**:
   - Testes automatizados que validam se o schema do banco corresponde aos models
   - Prevenir deploy se houver incompatibilidade

3. **Ambiente de desenvolvimento reproduzível**:
   - Usar Docker Compose para subir banco + aplicação
   - Garantir que todos os devs tenham o mesmo ambiente

## 🛠️ Próximos Passos (Melhoria Contínua)

Após resolver o problema imediato, considere implementar:

1. **Flyway para migrations** (adicionar no `pom.xml`):
   ```xml
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-core</artifactId>
   </dependency>
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-mysql</artifactId>
   </dependency>
   ```

2. **Docker Compose para ambiente local**:
   - MySQL + aplicação Spring Boot
   - Garante consistência entre todos os desenvolvedores

3. **Testes de integração**:
   - Testcontainers para rodar testes contra banco real
   - Validar queries JPA automaticamente
