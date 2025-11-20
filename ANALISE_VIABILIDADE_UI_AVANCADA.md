# Análise de Viabilidade: Interface Avançada de Evolução Fisioterapêutica

## 📊 Resumo Executivo

**Status Geral**: ⚠️ **PARCIALMENTE VIÁVEL** (60% implementável com estrutura atual)

A interface proposta é **tecnicamente viável**, mas requer **extensões significativas no modelo de dados** para atingir o nível de sofisticação desejado. O banco atual foi projetado para registro básico, não para análise clínica avançada.

---

## 🔍 Análise por Componente

### 1. CABEÇALHO DO PACIENTE ✅ **VIÁVEL (80%)**

#### O que funciona HOJE:
```sql
-- Dados disponíveis via JOIN
SELECT 
    P.NOMEPESSOA,           -- ✅ Nome completo
    P.DATANASCPES,          -- ✅ Idade (calculável)
    P.SEXOPESSOA,           -- ✅ Sexo
    PAC.RGPACIENTE,         -- ✅ RG
    PAC.STATUSPAC           -- ✅ Status ativo/inativo
FROM PACIENTE PAC
JOIN PESSOAFIS P ON PAC.ID_PESSOAFIS = P.IDPESSOAFIS
```

#### ❌ O que FALTA:
| Funcionalidade | Tabela Necessária | Status |
|---|---|---|
| **Foto do paciente** | Sem campo `FOTOPERFIL` em `PESSOAFIS` | ❌ NÃO EXISTE |
| **Diagnóstico principal** | Sem tabela `DIAGNOSTICO` ou campo em `ANAMNESE` | ❌ NÃO EXISTE |
| **Alertas críticos** | Sem tabela `ALERTA_CLINICO` ou `CONDICAO_MEDICA` | ❌ NÃO EXISTE |
| **Fase de tratamento** | Sem controle de sessões/fases | ❌ NÃO EXISTE |

#### 💡 Solução Proposta:
```sql
-- EXTENSÃO 1: Adicionar foto
ALTER TABLE PESSOAFIS ADD COLUMN FOTOPERFIL VARCHAR(500);

-- EXTENSÃO 2: Criar tabela de diagnósticos
CREATE TABLE DIAGNOSTICO (
    IDDIAGNOSTICO INT PRIMARY KEY AUTO_INCREMENT,
    ID_PACIENTE INT NOT NULL,
    CID10 VARCHAR(10),
    DESCRICAO VARCHAR(250) NOT NULL,
    DIAGNOSTPRINC BOOLEAN DEFAULT FALSE,
    DATACRIACAO DATE NOT NULL,
    FOREIGN KEY (ID_PACIENTE) REFERENCES PACIENTE(IDPACIENTE)
);

-- EXTENSÃO 3: Criar alertas clínicos
CREATE TABLE ALERTCLINICO (
    IDALERT INT PRIMARY KEY AUTO_INCREMENT,
    ID_PACIENTE INT NOT NULL,
    TIPOALERT ENUM('HIPERTENSAO','DIABETES','OSTEOPOROSE','ALERGIA','CIRURGIA_RECENTE','GESTANTE','OUTRO'),
    DESCRICAO VARCHAR(250),
    NIVELCRIT ENUM('BAIXO','MEDIO','ALTO','CRITICO'),
    ATIVO BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (ID_PACIENTE) REFERENCES PACIENTE(IDPACIENTE)
);

-- EXTENSÃO 4: Controle de fases
CREATE TABLE FASEPACIENTE (
    IDFASE INT PRIMARY KEY AUTO_INCREMENT,
    ID_PACIENTE INT NOT NULL,
    FASEATUAL ENUM('AGUDO','SUBAGUDO','CRONICO','MANUTENCAO','ALTA') NOT NULL,
    SESSAOATUAL INT NOT NULL DEFAULT 1,
    SESSAOTOTAL INT,
    OBJETIVOFASE VARCHAR(250),
    DATAINICIO DATE NOT NULL,
    FOREIGN KEY (ID_PACIENTE) REFERENCES PACIENTE(IDPACIENTE)
);
```

**Viabilidade**: ✅ Alta - Apenas adicionar campos/tabelas

---

### 2. PAINEL DE PROGRESSO (GRÁFICOS) ⚠️ **PARCIALMENTE VIÁVEL (40%)**

#### O que funciona HOJE:
```sql
-- Histórico de atendimentos (básico)
SELECT 
    DATAATENDI,
    DESCRATENDI
FROM ATENDIFISIO
WHERE ID_PACIENTE = ?
ORDER BY DATAATENDI DESC;
```

#### ❌ O que FALTA COMPLETAMENTE:

##### 2.1 Curva de Dor (EVA - Escala Visual Analógica)
**Problema**: `ATENDIFISIO.DESCRATENDI` é texto livre (VARCHAR 250). Não há campo estruturado para dor.

**Solução**:
```sql
-- Criar tabela de métricas por sessão
CREATE TABLE METRICASESSAO (
    IDMETRICA INT PRIMARY KEY AUTO_INCREMENT,
    ID_ATENDIFISIO INT NOT NULL,
    ESCALADOR TINYINT CHECK (ESCALADOR BETWEEN 0 AND 10),
    ESCALAESFORCO TINYINT CHECK (ESCALAESFORCO BETWEEN 0 AND 10),
    SENSACAO ENUM('PIOR','IGUAL','MELHOR','MUITO_MELHOR'),
    OBSERVACAO VARCHAR(250),
    FOREIGN KEY (ID_ATENDIFISIO) REFERENCES ATENDIFISIO(IDATENDIFISIO)
);
```

##### 2.2 Goniometria (Amplitude de Movimento - ADM)
**Problema**: Não existe nenhuma estrutura para medições objetivas.

**Solução**:
```sql
CREATE TABLE GONIOMETRIA (
    IDGONIO INT PRIMARY KEY AUTO_INCREMENT,
    ID_ATENDIFISIO INT NOT NULL,
    ARTICULACAO ENUM('OMBRO_DIR','OMBRO_ESQ','COTOVELO_DIR','COTOVELO_ESQ',
                     'PUNHO_DIR','PUNHO_ESQ','QUADRIL_DIR','QUADRIL_ESQ',
                     'JOELHO_DIR','JOELHO_ESQ','TORNOZELO_DIR','TORNOZELO_ESQ'),
    MOVIMENTO ENUM('FLEXAO','EXTENSAO','ABDUCAO','ADUCAO','ROTACAO_INT','ROTACAO_EXT'),
    GRAUS DECIMAL(5,2) NOT NULL,
    GRAUSMETA DECIMAL(5,2),
    OBSERVACAO VARCHAR(100),
    FOREIGN KEY (ID_ATENDIFISIO) REFERENCES ATENDIFISIO(IDATENDIFISIO)
);

-- Índice para buscar histórico de uma articulação
CREATE INDEX IDX_GONIO_ARTICULACAO ON GONIOMETRIA(ARTICULACAO, MOVIMENTO);
```

##### 2.3 Força Muscular
```sql
CREATE TABLE FORCAMUSCULAR (
    IDFORCA INT PRIMARY KEY AUTO_INCREMENT,
    ID_ATENDIFISIO INT NOT NULL,
    GRUPOMUSC VARCHAR(50) NOT NULL, -- Ex: 'Quadríceps', 'Bíceps'
    MEMBRO ENUM('DIR','ESQ','BILATERAL'),
    GRADOFORCA ENUM('0','1','2','3','4','5') NOT NULL, -- Escala MRC
    OBSERVACAO VARCHAR(100),
    FOREIGN KEY (ID_ATENDIFISIO) REFERENCES ATENDIFISIO(IDATENDIFISIO)
);
```

**Viabilidade**: ⚠️ Média - Requer refatoração significativa do modelo

---

### 3. TIMELINE LATERAL ✅ **VIÁVEL (70%)**

#### O que funciona HOJE:
```sql
-- Lista de atendimentos em ordem cronológica
SELECT 
    A.IDATENDIFISIO,
    A.DATAATENDI,
    A.DESCRATENDI,
    PR.DESCRPROC AS PROCEDIMENTO
FROM ATENDIFISIO A
JOIN PROCEDIMENTO PR ON A.ID_PROCED = PR.IDPROCED
WHERE A.ID_PACIENTE = ?
ORDER BY A.DATAATENDI DESC;
```

#### ❌ O que FALTA:

##### 3.1 Ícones/Tags de Condutas
**Problema**: `ID_PROCED` aponta para procedimento genérico, sem granularidade de condutas.

**Solução**:
```sql
CREATE TABLE CONDUTASESSAO (
    IDCONDUTA INT PRIMARY KEY AUTO_INCREMENT,
    ID_ATENDIFISIO INT NOT NULL,
    TIPOCONDUTA ENUM('ELETROTERAPIA','CINESIOTERAPIA','TERAPIA_MANUAL',
                     'CRIOTERAPIA','TERMOTERAPIA','HIDROTERAPIA','PILATES',
                     'RPG','BANDAGEM','OUTRO'),
    ESPECIFICACAO VARCHAR(250), -- Ex: "TENS 100Hz, 30min"
    DURACAO INT, -- Em minutos
    FOREIGN KEY (ID_ATENDIFISIO) REFERENCES ATENDIFISIO(IDATENDIFISIO)
);
```

##### 3.2 Intercorrências (Alertas na Timeline)
```sql
CREATE TABLE INTERCORRENCIA (
    IDINTERCORR INT PRIMARY KEY AUTO_INCREMENT,
    ID_ATENDIFISIO INT NOT NULL,
    TIPO ENUM('DOR_AGUDA','QUEDA','REACAO_ADVERSA','PIORA_QUADRO','OUTRO'),
    GRAVIDADE ENUM('LEVE','MODERADA','GRAVE'),
    DESCRICAO TEXT NOT NULL,
    CONDUTATOMADA VARCHAR(250),
    FOREIGN KEY (ID_ATENDIFISIO) REFERENCES ATENDIFISIO(IDATENDIFISIO)
);
```

**Viabilidade**: ✅ Alta - Extensões diretas

---

### 4. MÉTODO SOAP (Registro Estruturado) ⚠️ **PARCIALMENTE VIÁVEL (50%)**

#### Estado Atual vs Necessário:

| Componente | Campo Atual | O que Falta |
|---|---|---|
| **S (Subjetivo)** | `DESCRATENDI` (texto livre) | ✅ Slider de dor<br>✅ Tags rápidas<br>❌ Estruturado |
| **O (Objetivo)** | ❌ Nenhum | ❌ Mapa corporal<br>❌ Checklist condutas<br>❌ Anexos foto/vídeo |
| **A (Avaliação)** | `DESCRATENDI` (texto livre) | ✅ Campo texto<br>❌ Comparativo automático |
| **P (Plano)** | ❌ Nenhum | ❌ Próximos passos<br>❌ Objetivos mensuráveis |

#### Solução Completa:

```sql
-- Refatorar ATENDIFISIO para SOAP estruturado
ALTER TABLE ATENDIFISIO 
    ADD COLUMN SUBJETIVO TEXT,           -- S: Queixa do paciente
    ADD COLUMN OBJETIVO TEXT,            -- O: Observações clínicas
    ADD COLUMN AVALIACAO TEXT,           -- A: Análise do fisio
    ADD COLUMN PLANO TEXT;               -- P: Conduta futura

-- Tags rápidas
CREATE TABLE TAGSSUBJETIVO (
    IDTAG INT PRIMARY KEY AUTO_INCREMENT,
    ID_ATENDIFISIO INT NOT NULL,
    TAG ENUM('DORMIU_BEM','SEM_DOR_REPOUSO','TOMOU_ANALGESICO',
             'DOR_NOTURNA','DOR_AO_MOVIMENTO','EDEMA','RIGIDEZ'),
    FOREIGN KEY (ID_ATENDIFISIO) REFERENCES ATENDIFISIO(IDATENDIFISIO)
);

-- Mapa corporal (Body Map)
CREATE TABLE MAPACORPORAL (
    IDMAPA INT PRIMARY KEY AUTO_INCREMENT,
    ID_ATENDIFISIO INT NOT NULL,
    REGIAO ENUM('CERVICAL','TORACICA','LOMBAR','OMBRO_DIR','OMBRO_ESQ',
                'COTOVELO_DIR','COTOVELO_ESQ','PUNHO_DIR','PUNHO_ESQ',
                'QUADRIL_DIR','QUADRIL_ESQ','JOELHO_DIR','JOELHO_ESQ',
                'TORNOZELO_DIR','TORNOZELO_ESQ'),
    ACHADO ENUM('EDEMA','CONTRATURA','HEMATOMA','CICATRIZ','DEFORMIDADE','DOR_PALPACAO'),
    INTENSIDADE ENUM('LEVE','MODERADO','GRAVE'),
    OBSERVACAO VARCHAR(250),
    FOREIGN KEY (ID_ATENDIFISIO) REFERENCES ATENDIFISIO(IDATENDIFISIO)
);

-- Anexos (Fotos/Vídeos)
CREATE TABLE ANEXOSESSAO (
    IDANEXO INT PRIMARY KEY AUTO_INCREMENT,
    ID_ATENDIFISIO INT NOT NULL,
    TIPOANEXO ENUM('FOTO','VIDEO','AUDIO'),
    URL VARCHAR(500) NOT NULL,
    DESCRICAO VARCHAR(250),
    DATAHORA DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (ID_ATENDIFISIO) REFERENCES ATENDIFISIO(IDATENDIFISIO)
);
```

**Viabilidade**: ⚠️ Média - Requer refatoração do modelo core

---

### 5. DIFERENCIAIS DE UX 🚀 **VIÁVEL COM INVESTIMENTO (30%)**

#### 5.1 Comparativo Visual (Antes x Depois)
**Solução**: Usar `ANEXOSESSAO` + flag `TIPOCOMPARATIVO`

```sql
ALTER TABLE ANEXOSESSAO 
    ADD COLUMN TIPOCOMPARATIVO ENUM('AVALIACAO_INICIAL','REAVALIACAO','ALTA','EVOLUCAO');
```

**Viabilidade**: ✅ Alta (depende de upload de imagens)

---

#### 5.2 Smart Templates
**Problema**: Não existe vínculo entre `DIAGNOSTICO` → `EXERCICIOS` ou `PROCEDIMENTOS` recomendados.

**Solução**:
```sql
CREATE TABLE PROTOCOLOCLINICO (
    IDPROTOCOLO INT PRIMARY KEY AUTO_INCREMENT,
    CID10 VARCHAR(10),
    NOMEPROTOCOLO VARCHAR(250) NOT NULL,
    DESCRICAO TEXT
);

CREATE TABLE PROTOCOLOEXERC (
    IDPROTOCOLOEXERC INT PRIMARY KEY AUTO_INCREMENT,
    ID_PROTOCOLO INT NOT NULL,
    ID_EXERCICIO INT NOT NULL,
    ORDEM INT,
    SERIES INT,
    REPETICOES INT,
    FOREIGN KEY (ID_PROTOCOLO) REFERENCES PROTOCOLOCLINICO(IDPROTOCOLO),
    FOREIGN KEY (ID_EXERCICIO) REFERENCES EXERCICIO(IDEXERCICIO)
);

CREATE TABLE PROTOCOLOCONDUTA (
    IDPROTOCOLOCOND INT PRIMARY KEY AUTO_INCREMENT,
    ID_PROTOCOLO INT NOT NULL,
    TIPOCONDUTA ENUM('ELETROTERAPIA','CINESIOTERAPIA','TERAPIA_MANUAL',...),
    ESPECIFICACAO VARCHAR(250),
    FOREIGN KEY (ID_PROTOCOLO) REFERENCES PROTOCOLOCLINICO(IDPROTOCOLO)
);
```

**Viabilidade**: ⚠️ Média - Requer banco de conhecimento (Big Data)

---

#### 5.3 Ditado por Voz
**Solução**: Frontend com API Web Speech ou integração com Azure Speech/Google Cloud STT.

**Impacto no Banco**: ❌ Nenhum - Apenas transforma áudio em texto.

**Viabilidade**: ✅ Alta (custo de API)

---

## 📋 PLANO DE IMPLEMENTAÇÃO

### Fase 1: FUNDAÇÃO (2-3 semanas)
**Objetivo**: Tornar a estrutura atual utilizável

```sql
-- 1. Adicionar foto ao paciente
ALTER TABLE PESSOAFIS ADD COLUMN FOTOPERFIL VARCHAR(500);

-- 2. Criar diagnósticos
CREATE TABLE DIAGNOSTICO (...);

-- 3. Criar alertas clínicos
CREATE TABLE ALERTCLINICO (...);

-- 4. Adicionar métricas básicas
CREATE TABLE METRICASESSAO (...);
ALTER TABLE ATENDIFISIO ADD COLUMN ESCALADOR TINYINT;

-- 5. Estruturar SOAP
ALTER TABLE ATENDIFISIO 
    ADD COLUMN SUBJETIVO TEXT,
    ADD COLUMN OBJETIVO TEXT,
    ADD COLUMN AVALIACAO TEXT,
    ADD COLUMN PLANO TEXT;
```

### Fase 2: MEDIÇÕES OBJETIVAS (3-4 semanas)
```sql
CREATE TABLE GONIOMETRIA (...);
CREATE TABLE FORCAMUSCULAR (...);
CREATE TABLE MAPACORPORAL (...);
CREATE TABLE CONDUTASESSAO (...);
```

### Fase 3: RECURSOS AVANÇADOS (4-6 semanas)
```sql
CREATE TABLE ANEXOSESSAO (...);
CREATE TABLE INTERCORRENCIA (...);
CREATE TABLE PROTOCOLOCLINICO (...);
CREATE TABLE FASEPACIENTE (...);
```

### Fase 4: FRONTEND AVANÇADO (6-8 semanas)
- Implementar gráficos (Chart.js / D3.js)
- Body Map interativo (SVG + JavaScript)
- Upload de imagens/vídeos (AWS S3 / Cloudinary)
- Ditado por voz (Web Speech API)

---

## 🎯 RECOMENDAÇÃO FINAL

### ✅ O que PODE ser feito AGORA (com banco atual):
1. Timeline básica de atendimentos
2. Lista de exercícios prescritos
3. Histórico textual (SOAP em texto livre)
4. Dados demográficos do paciente

### ⚠️ O que REQUER EXTENSÃO (viável, mas precisa de schema):
1. Gráficos de evolução (dor, ADM, força)
2. Mapa corporal
3. Alertas clínicos
4. Anexos de imagens
5. Templates inteligentes

### ❌ O que NÃO É POSSÍVEL (sem refatoração profunda):
1. Comparativos automáticos de medições
2. Inteligência preditiva (sugestões baseadas em IA)
3. Análise biomecânica avançada

---

## 💰 ESTIMATIVA DE CUSTO (DESENVOLVIMENTO)

| Item | Horas | Complexidade |
|---|---|---|
| Extensão do banco (Fases 1-3) | 80-120h | Média |
| APIs REST (novos endpoints) | 60-80h | Média |
| Frontend avançado | 120-160h | Alta |
| Integração de gráficos | 40-60h | Média |
| Upload de mídia | 30-40h | Baixa |
| Ditado por voz | 20-30h | Baixa |
| **TOTAL** | **350-490h** | **8-12 semanas** |

---

## 🚀 PRÓXIMOS PASSOS RECOMENDADOS

1. **CURTO PRAZO** (1-2 semanas):
   - Criar script SQL com extensões da Fase 1
   - Prototipar a tela de evolução com dados mockados
   - Validar wireframe com fisioterapeutas reais

2. **MÉDIO PRAZO** (1-2 meses):
   - Implementar backend (Spring Boot) com novas entidades
   - Desenvolver frontend com gráficos básicos
   - Testar com 5-10 pacientes reais

3. **LONGO PRAZO** (3-6 meses):
   - Adicionar recursos avançados (body map, ditado)
   - Integrar protocolos clínicos baseados em evidências
   - Implementar analytics e relatórios

---

## ✍️ CONCLUSÃO

A interface proposta é **ambiciosa e alinhada com as melhores práticas da fisioterapia moderna**. O banco de dados atual serve como uma **base sólida**, mas foi projetado para "registro básico", não para "análise clínica".

**Viabilidade Geral**: 60% implementável com extensões SQL + 40% requer redesenho.

**Recomendação**: Implementar em **fases incrementais**, priorizando:
1. Métricas estruturadas (dor, ADM)
2. SOAP estruturado
3. Gráficos de evolução
4. Recursos visuais (body map, fotos)

**ROI Esperado**: Alta satisfação clínica + diferencial competitivo significativo.
