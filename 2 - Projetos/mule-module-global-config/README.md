# 🛠️ Mule Global Configuration Module (v1.0)

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:0C2340,100:00BFFF&height=200&section=header&text=Mule+Global+Config+Module&fontSize=38&fontColor=ffffff&animation=fadeIn" alt="Banner" />
</p>

<p align="center">
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-3.6.x--3.9.x-red?logo=apache-maven" /></a>
  <a href="https://adoptium.net/"><img src="https://img.shields.io/badge/Java-17-blue?logo=java" /></a>
  <a href="https://www.typesafe.com/"><img src="https://img.shields.io/badge/Typesafe_Config-1.4.3-blue?logo=typesafe" /></a>
  <a href="https://github.com/everit-org/json-schema"><img src="https://img.shields.io/badge/Everit_JSON--Schema-1.14.6-yellow?logo=json" /></a>
</p>

---

## 📦 Visão Geral

Este módulo fornece uma solução completa para **carregar e validar configurações globais** do Mule Runtime.

Através de **construtores dinâmicos**, ele permite:

- 🔧 **Carregamento de configurações** a partir de arquivos JSON (`.json`) no classpath ou em `MULE_HOME/conf`.
- 🗝️ **Validação de esquema JSON** usando `mule-schema.json` e Everit JSON Schema.
- 🔄 **Recarregamento seguro** (thread-safe) das configurações em tempo de execução.
- ⚙️ **Construção dinâmica** de objetos de configuração Maven e Cluster.
- 🛡️ **Tratamento de exceções** centralizado via `RuntimeGlobalConfigException`.
- 🔒 **Lock otimizado** com `StampedLock` para acesso seguro em múltiplas threads.

Ideal para ambientes complexos de integração que exigem:

- **Múltiplos perfis** de configuração (DEV, HML, PROD).
- **Fallback** de configurações padrão quando valores não são fornecidos.
- **Validação prévia** de formato e conteúdo antes de iniciar fluxos Mule.

---

## 🧰 Funcionalidades

| Recurso                                | Detalhes                                                                                 |
|----------------------------------------|------------------------------------------------------------------------------------------|
| 📂 Carregamento de configuração JSON   | Usa `ConfigFactory.load()` do Typesafe Config para ler `mule-config.json`.               |
| 🔍 Validação de esquema                | Usa `Everit JSON Schema` para validar contra `mule-schema.json`.                         |
| 🔁 Recarregamento seguro               | `reset()` revalida e recarrega configs em runtime com lock otimizado (`StampedLock`).    |
| ☁️ Perfis ativos/inativos              | Suporta `activeProfiles` e `inactiveProfiles` para controle de ambiente.                  |
| 🔧 Configuração Maven dinâmica         | `MavenConfigBuilder.buildMavenConfig()` monta `MavenConfiguration` completo.             |
| 🌐 Configuração de Cluster             | `ClusterConfigBuilder.parseClusterConfig()` monta objetos de `ClusterConfig`.            |
| 🔐 Tratamento de erros centralizado    | `RuntimeGlobalConfigException` captura falhas de schema, I/O e casting.                 |
| 📜 Logging estruturado                 | Usa SLF4J com placeholders (`{}`) e verifica `isDebugEnabled()`.                          |
| 📦 Dependências gerenciadas            | Centraliza versões no `pom.xml` e usa repositórios MuleSoft EE e Maven Central.         |
| 🧪 Testes unitários                    | Suporte a `mule-tests-unit` e relatórios `mule-tests-allure`.                            |

---

## 📂 Estrutura do Projeto

```text
mule-module-global-config/           # Raiz do projeto
├── pom.xml                         # Build Maven e dependências
├── mule-artifact.json              # Metadados do pacote Mule
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/mule/runtime/globalconfig/api/
│   │   │       ├── GlobalConfigLoader.java      # Carrega configs globais
│   │   │       ├── RuntimeGlobalConfigException.java  # Exceção customizada
│   │   │       ├── EnableableConfig.java        # Interface de config habilitável
│   │   │       ├── ClusterConfig.java           # POJO de config de cluster
│   │   │       └── DefaultEnableableConfig.java # Implementação default
│   │   ├── resources/
│   │   │   └── mule-schema.json        # Esquema JSON para validação
│   │   └── mule-module-global-config.yaml  # Configuração de policy (YAML)
│   └── test/
│       └── munit/                      # Testes MUnit
└── README.md                           # Documentação deste módulo
```

---

## 🚀 Como Executar

1. **Clone o repositório**
   ```bash
   git clone https://github.com/SUA_ORG/mule-module-global-config.git
   cd mule-module-global-config
   ```
2. **Compile o módulo**
   ```bash
   mvn clean install -DskipTests
   ```
3. **Execute testes unitários**
   ```bash
   mvn test
   ```
4. **Instale no Anypoint Exchange**
    - **Cenário `.m2/settings.xml`**
      ```bash
      mvn clean deploy -DskipTests
      ```
    - **Cenário `.maven/settings.xml`**
      ```bash
      mvn clean deploy -s .maven/settings.xml -DskipTests
      ```

> 🔑 **Dica:** defina `ANYPOINT_USERNAME` e `ANYPOINT_PASSWORD` como variáveis de ambiente ou no `settings.xml`.

---

## ⚙️ Configuração de Uso

O arquivo de configuração principal é `mule-config.json`, com a estrutura:

```json
{
  "muleRuntimeConfig": {
    "maven": {
      "globalSettingsLocation": "/home/mule/.m2/settings.xml",
      "ignoreArtifactDescriptorRepositories": true,
      "activeProfiles": ["dev","qa"],
      "repositories": {
        "central": {
          "url": "https://repo.maven.apache.org/maven2",
          "snapshotPolicy": { "enabled": true, "updatePolicy": "always" }
        }
      }
    },
    "cluster": {
      "enabled": true,
      "serviceName": "my-service",
      "nodes": ["node1.example.com","node2.example.com"]
    }
  }
}
```

- **`muleRuntimeConfig.maven`** → Configurações Maven (arquivo global, repos, perfis).
- **`muleRuntimeConfig.cluster`** → Configurações de cluster (habilitar, nós).

---

## 🔍 Exemplo de Uso

1. **Configuração mínima** (somente Maven default):
   ```json
   { "muleRuntimeConfig": {} }
   ```
    - Carrega configurações padrão via `defaultMavenConfig()` e `defaultClusterConfig()`.

2. **Configuração completa**:

   ```json
   {
     "muleRuntimeConfig": {
       "maven": {
         "repositoryLocation": "/opt/mule/repository",
         "activeProfiles": ["prod"],
         "offlineMode": false
       },
       "cluster": {
         "enabled": false
       }
     }
   }
   ```

---

## 📜 Exemplo de Output no Log

```
INFO  [main] GlobalConfigLoader - Inicializando configuração global...
DEBUG [main] GlobalConfigLoader - Using effective mule-config.json configuration:
{ "muleRuntimeConfig" : { "maven" : { ... } } }
INFO  [main] GlobalConfigLoader - Configuração Maven carregada com 2 repositórios.
INFO  [main] GlobalConfigLoader - Configuração de cluster habilitada: true.
```

---

## 📋 Requisitos

- **Java JDK** 8 ou superior
- **Maven** 3.6.x → 3.9.x
- Acesso à internet para download de dependências
- **Variáveis de ambiente**:
    - `MULE_HOME` (opcional, padrão de diretório do Mule)
    - `ANYPOINT_USERNAME` e `ANYPOINT_PASSWORD` para publicação no Exchange

---

## 🧪 Testes e CI/CD

- Testes MUnit em `src/test/munit`.
- Exemplos de pipeline:

**GitHub Actions**:

```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main ]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - name: Setup Java
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '11'
      - name: Cache Maven
        uses: actions/cache@v4
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
      - name: Build & Test
        run: mvn clean verify
      - name: Publish to Exchange
        run: mvn clean deploy -s .maven/settings.xml -DskipTests
```

---

## 👨‍💼 Desenvolvedor Responsável

- **Leonel Dorneles Porto**
- **Email:** leonel.d.porto@accenture.com
- **LinkedIn:** https://br.linkedin.com/in/leonel-dorneles-porto-b88600122
- **Organização:** Accenture / Telefônica VIVO

---

<p align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&size=20&pause=1000&color=00BFFF&center=true&vCenter=true&width=1000&lines=Mule+Global+Config+Module+feito+com+💙+para+integra%C3%A7%C3%B5es+MuleSoft!" alt="Typing SVG" />
</p>

---