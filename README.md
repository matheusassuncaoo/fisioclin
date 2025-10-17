# 💚 Fisioclin Backend

**API REST para o sistema clínico de Fisioterapia "Fisioclin", construída com Java 25 e Spring Boot.**

[Sobre](#-sobre-o-projeto) • [Roadmap](#-roadmap-de-funcionalidades) • [Tecnologias](#-tecnologias) • [Como Executar](#%EF%B8%8F-como-executar) • [Contribuidores](#-contribuidores)

![Java](https://img.shields.io/badge/Java-JDK_25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring_Boot-3.5.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-4.0.0-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)

![Build Status](https://img.shields.io/github/actions/workflow/status/SEU_USUARIO/fisioclin/main.yml?style=for-the-badge&branch=main)
![Licença](https://img.shields.io/github/license/SEU_USUARIO/fisioclin?style=for-the-badge)
![Último Commit](https://img.shields.io/github/last-commit/SEU_USUARIO/fisioclin?style=for-the-badge)

---

## 🎯 Sobre o Projeto

A **API Fisioclin** é o backend de um sistema clínico voltado para Fisioterapia. A aplicação permite gerenciamento completo de pacientes, profissionais, agendamentos, prontuários e faturamento, fornecendo uma base sólida e escalável para qualquer frontend (web ou mobile).

Este projeto foi desenvolvido seguindo as melhores práticas de APIs REST, com persistência via Spring Data JDBC para alta performance e baixa complexidade, usando MySQL como banco de dados relacional.

---

## 🗺️ Roadmap de Funcionalidades

Este é o planejamento de entregas do projeto. Conforme as funcionalidades forem implementadas, os itens serão marcados.

- [ ] **Módulo de Pacientes:** CRUD completo, busca por CPF e histórico clínico.
- [ ] **Módulo de Profissionais:** CRUD completo, especialidades e agenda.
- [ ] **Módulo de Agenda:** Criação, reagendamento e controle de status de consultas.
- [ ] **Prontuário Eletrônico:** Registros de evolução, anexos e permissões.
- [ ] **Faturamento:** Cadastro de serviços, emissão de recibos e relatórios simples.
- [ ] **Validações:** Bean Validation em entidades e DTOs.
- [ ] **Tratamento de Exceções:** Handlers padronizados com RFC 7807 (Problem Details).
- [ ] **Segurança:** Autenticação e autorização com Spring Security e JWT.
- [ ] **Observabilidade:** Logs estruturados e correlation-id para rastreamento.
- [ ] **Documentação:** OpenAPI/Swagger UI disponível em `/swagger-ui/index.html`.

---

## 🚀 Tecnologias

As seguintes ferramentas e tecnologias foram utilizadas na construção do projeto:

- **Java JDK 25** - Linguagem de programação
- **Spring Boot 3.5.x** - Framework para aplicações Java
- **Spring Web** - Módulo MVC para APIs REST
- **Spring Data JDBC** - Persistência JDBC simplificada
- **HikariCP** - Pool de conexões (padrão no Spring Boot)
- **MySQL 8.0** - Banco de dados relacional
- **Maven** - Gerenciamento de dependências
- **Lombok** - Redução de boilerplate code
- **Spring Boot DevTools** - Hot reload em desenvolvimento
- **Jakarta Validation** - Validação de beans
- **SpringDoc OpenAPI** - Documentação automática da API

---

## 🛠️ Como Executar

Siga os passos abaixo para configurar e executar o projeto localmente.

### Pré-requisitos

Antes de começar, você vai precisar ter instalado:

- [Java JDK 25](https://www.oracle.com/br/java/technologies/downloads/)
- [Apache Maven 3.9+](https://maven.apache.org/download.cgi)
- [MySQL Server 8.0+](https://dev.mysql.com/downloads/mysql/)
- [Git](https://git-scm.com/downloads)
- Um cliente de API, como [Postman](https://www.postman.com/downloads/) ou [Insomnia](https://insomnia.rest/download)

### Passo a Passo

**1. Clone o repositório:**
git clone https://github.com/SEU_USUARIO/fisioclin.git
cd fisioclin


**2. Configure o Banco de Dados:**
Crie o banco de dados e um usuário específico no MySQL. Execute os comandos abaixo no seu cliente MySQL (MySQL Workbench, DBeaver, etc.)


