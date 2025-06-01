---
# 🔄 API Gateway HTTP Policy Transform Extension  
### Extensão Customizada Mule para Manipulação de Atributos HTTP em Políticas

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:0C2340,100:00BFFF&height=220&section=header&text=Transforma%C3%A7%C3%A3o%20HTTP%20em%20Policies&fontSize=38&fontColor=ffffff&animation=fadeIn" alt="Extension Banner" />
</p>

<p align="center">
  <a href="https://docs.mulesoft.com/mule-sdk/latest/"><img src="https://img.shields.io/badge/Mule%20SDK-M%C3%B3dulo%20de%20Extens%C3%A3o-003B71?logo=mulesoft" /></a>
  <a href="https://openjdk.org/projects/jdk/8/"><img src="https://img.shields.io/badge/Java-8%20%2F%2011-FF6A00?logo=java" /></a>
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-3.x-C71A36?logo=apache-maven" /></a>
</p>

---

## 📑 Sumário
* [Visão Geral](#visão-geral)  
* [Motivação](#motivação)  
* [Funcionalidades](#funcionalidades)  
* [Arquitetura](#arquitetura)  
* [Estrutura do Projeto](#estrutura-do-projeto)  
* [Diagrama Detalhado de Fábricas e Handlers](#diagrama-fábricas-handlers)  
* [Operações Disponíveis](#operações-disponíveis)  
* [Exemplos de Uso](#exemplos)  
* [Instalação & Build](#instalação)  
* [Publicação no Exchange](#publicação)  
* [Integração com Extensão de Atributos](#integração)  
* [FAQ](#faq)  
* [Roadmap](#roadmap)  
* [Troubleshooting](#troubleshooting)  
* [Contribuindo](#contribuindo)  
* [Licença](#licença)  
* [Contato](#contato)  

---

## 📖 Visão Geral <a id="visão-geral"></a>

A **HTTP Policy Transform Extension** é um módulo Mule 4 capaz de **ler, alterar, remover ou acrescentar** cabeçalhos HTTP e outros metadados **dentro de políticas customizadas** do API Gateway.  
Diferente de transformações em flows, aplicar a lógica na camada de policy garante governança centralizada, reutilização e zero impacto no código das APIs.

---

## 💡 Motivação <a id="motivação"></a>

| Cenário                    | Descrição                                                                                                                                      |
|----------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| Multi-IdP / Zero-Trust     | Políticas podem exigir cabeçalhos especiais para escolher o JWKS ou IdP corretos antes de validar o JWT.                                       |
| Observabilidade            | Adicionar _trace headers_ (`X-Correlation-Id`) em todas as requisições/respostas na borda do gateway.                                         |
| Segurança                  | Remover `Server`, `X-Powered-By` ou outros cabeçalhos de fingerprinting antes da resposta deixar a nuvem.                                     |
| LGPD / PCI                 | Mascarar dados sensíveis em cabeçalhos antes de armazenar em logs.                                                                            |
| Roteamento Dinâmico        | Redirecionar chamadas internas de acordo com path, método ou query param capturados.                                                          |

---

## 🚀 Funcionalidades <a id="funcionalidades"></a>

* **Operação 1 — `get-request-attributes`**  
  Devolve um wrapper com headers, queryParams, pathParams e campos técnicos.  
* **Operação 2 — `transform-request`**  
  Permite *add/replace/remove* cabeçalhos **antes** da API.  
* **Operação 3 — `transform-response`**  
  Permite *add/replace/remove* cabeçalhos **antes** de retornar ao consumidor.  
* **Handler Selector Automático** — escolhe Listener, Requester ou Policy dinamicamente.  
* **Limite de Headers** — `LimitedHttpHeadersMultimapFactory` evita overload de memória.  
* **Enum de Erros Customizados** — `INVALID_HEADER`, `MISSING_VALUE`, `LIMIT_EXCEEDED` etc.  
* **Metadata Resolver** para o Design Center exibir autocomplete e docs.  

---

## 🏗️ Arquitetura High-Level <a id="arquitetura"></a>

```text
┌──── Client ────┐
│  (HTTP)        │
└──────┬─────────┘
       │
       ▼
┌────────────── API Gateway ──────────────┐
│                                         │
│ 1. http-attrs:get-request-attributes     │
│ 2. http-transform:transform-request      │
│ 3. (outras políticas)                    │
│ 4. API Implementation (Flow)             │
│ 5. http-transform:transform-response     │
└──────────────────────────────────────────┘
````

---

## 📂 Estrutura do Projeto <a id="estrutura-do-projeto"></a>

| Arquivo / Pasta                                      | Função                                                    |
| ---------------------------------------------------- | --------------------------------------------------------- |
| **`pom.xml`**                                        | Build Maven (packaging `mule-extension`)                  |
| **Extension Core**                                   |                                                           |
| `HttpPolicyTransformExtension.java`                  | Classe `@Extension` principal                             |
| `HttpTransformOperations.java`                       | Implementa operações                                      |
| `Header.java`                                        | `<header key="" value="" action="ADD\|REPLACE\|REMOVE"/>` |
| **Wrapper**                                          |                                                           |
| `HttpRequestAttributesWrapper.java`                  | POJO de atributos HTTP                                    |
| **Selector & Contracts**                             |                                                           |
| `HttpAttributesHandlerSelector.java`                 | Decide handler ideal                                      |
| `HttpAttributesHandler.java`                         | Interface geral                                           |
| **Request Handlers**                                 |                                                           |
| `HttpRequestAttributesHandler.java`                  | Listener externo                                          |
| `HttpRequesterRequestAttributesHandler.java`         | HTTP Requester                                            |
| `HttpPolicyRequestAttributesHandler.java`            | Policy (request)                                          |
| **Response Handlers**                                |                                                           |
| `HttpResponseAttributesHandler.java`                 | Listener externo                                          |
| `HttpPolicyResponseAttributesHandler.java`           | Policy (response)                                         |
| **Factories (Request)**                              |                                                           |
| `HttpPolicyRequestAttributesFactory.java`            | Interface                                                 |
| `HttpPolicyRequestAttributesDefaultFactory.java`     | Default                                                   |
| `HttpPolicyRequestAttributesReflectiveFactory.java`  | Reflection                                                |
| **Factories (Response)**                             |                                                           |
| `HttpPolicyResponseAttributesFactory.java`           | Interface                                                 |
| `HttpPolicyResponseAttributesDefaultFactory.java`    | Default                                                   |
| `HttpPolicyResponseAttributesReflectiveFactory.java` | Reflection                                                |
| `HttpPolicyResponseAttributesHandlerFactory.java`    | Cria handlers                                             |
| **Factories (Requester)**                            |                                                           |
| `HttpRequesterRequestAttributesHandlerFactory.java`  | Factory Requester                                         |
| **Factories (Genéricas)**                            |                                                           |
| `HttpResponseAttributesFactory.java`                 | Raiz resposta                                             |
| `HttpResponseAttributesDefaultFactory.java`          | Default listener                                          |
| `HttpResponseAttributesReflectiveFactory.java`       | Reflection listener                                       |
| **Headers Multimap**                                 |                                                           |
| `LimitedHttpHeadersMultimapFactory.java`             | Multimap com limite                                       |
| **Reflection Utils**                                 |                                                           |
| `FieldInspector.java`                                | Descobre campos                                           |
| `FieldCopier.java`                                   | Copia fields                                              |
| `CrossClassFieldCopier.java`                         | Copia inter-classes                                       |
| **Erro & Metadata**                                  |                                                           |
| `HttpPolicyTransformErrorTypes.java`                 | Enum de erros                                             |
| `HttpPolicyTransformErrorTypeProvider.java`          | Provider de erros                                         |
| `HttpPolicyTransformMetadataResolver.java`           | Metadados p/ DC                                           |

---
## 🔍 Diagrama Detalhado de Fábricas e Handlers <a id="diagrama-fábricas-handlers"></a>

```text
                            ┌────────────────────────────────────────────┐
                            │   HttpAttributesHandlerSelector            │
                            │   • Detecta tipo de execução:              │
                            │     - ContextType = LISTENER / REQUESTER   │
                            │     - ExecutionPhase = POLICY / FLOW       │
                            └────────────────────┬───────────────────────┘
                                                 │ decide()
                    ┌────────────────────────────┼────────────────────────────┐
                    ▼                            ▼                            ▼
       ┌────────────────────────┐  ┌──────────────────────────┐  ┌────────────────────────────────┐
       │   Request Handlers     │  │   Response Handlers      │  │   Requester Handler            │
       │  (Listener / Policy)   │  │  (Listener / Policy)     │  │  (HTTP Requester)              │
       └────────────┬───────────┘  └────────────┬─────────────┘  └──────────────┬─────────────────┘
                    │ factory()                 │ factory()                     │ factory()
                    ▼                           ▼                               ▼
        ┌────────────────────────────┐  ┌────────────────────────────┐  ┌────────────────────────────────────┐
        │ HttpPolicyRequestAttributes│  │HttpPolicyResponseAttributes│  │ HttpRequesterRequestAttributes     │
        │ Handler                    │  │Handler                     │  │ Handler                            │
        └────────────┬───────────────┘  └────────────┬───────────────┘  └────────────────────────────────────┘
                     │                               │
        ┌────────────▼────────────┐     ┌────────────▼────────────┐
        │  Factory Selector       │     │  Factory Selector       │
        └────────────┬────────────┘     └────────────┬────────────┘
                     │ chooses                       │ chooses
        ┌────────────▼────────────┐     ┌────────────▼────────────┐
        │  DefaultFactory         │     │  DefaultFactory         │
        └────────────┬────────────┘     └────────────┬────────────┘
                     │ fallback                      │ fallback
        ┌────────────▼────────────┐     ┌────────────▼────────────┐
        │ ReflectiveFactory       │     │ ReflectiveFactory       │
        └─────────────────────────┘     └─────────────────────────┘

Legenda:
- "DefaultFactory": acesso direto via API Gateway SDK
- "ReflectiveFactory": fallback baseado em introspecção Java (Reflection)
- "Factory Selector": lógica condicional que tenta usar a default e recorre à alternativa se necessário
```

---

## ⚙️ Operações Disponíveis <a id="operações-disponíveis"></a>

### 1 ▪ `get-request-attributes`

Retorna em `target` o objeto `HttpRequestAttributesWrapper`.

### 2 ▪ `transform-request`

| Atributo | Obrigatório | Descrição                                  | Observações                         |
| -------- | ----------- | ------------------------------------------ | ----------------------------------- |
| `key`    | ✔           | Nome do header                             | Case-insensitive                    |
| `value`  | ✖           | Novo valor                                 | Ignorado se `action="REMOVE"`       |
| `action` | ✖           | `ADD`\|`REPLACE`\|`REMOVE` (default `ADD`) | `REPLACE` troca primeira ocorrência |

### 3 ▪ `transform-response`

Mesmos atributos, aplicados à resposta.

---

## 🧪 Exemplos de Uso <a id="exemplos"></a>

```xml
<configuration>
  <http-attrs:get-request-attributes target="reqAttrs"/>

  <http-transform:transform-request>
    <http-transform:headers>
      <http-transform:header key="X-Tenant" value="#[vars.reqAttrs.headers['tenant']]"/>
      <http-transform:header key="X-Env" value="HML"/>
    </http-transform:headers>
  </http-transform:transform-request>

  <!-- … chamada da API … -->

  <http-transform:transform-response>
    <http-transform:headers>
      <http-transform:header key="Server" action="REMOVE"/>
      <http-transform:header key="X-Correlation-Id"
                              value="#[vars.reqAttrs.headers.correlation-id default uuid()]"/>
    </http-transform:headers>
  </http-transform:transform-response>
</configuration>
```

---

## 🏗️ Instalação & Build <a id="instalação"></a>

```bash
git clone https://github.com/<sua-org>/mule-http-policy-transform-extension.git
cd mule-http-policy-transform-extension
mvn clean install -DskipTests
```

---

## ☁️ Publicação no Exchange <a id="publicação"></a>

```bash
mvn clean deploy -s .maven/settings.xml
```

---

## 🔗 Integração com a Extensão de Atributos HTTP <a id="integração"></a>

Esta extensão **depende** de:
[`api-gateway-http-policy-attributes-extension`](https://github.com/LeonelIntegrationXpert/api-gateway-http-policy-attributes-extension)

> 📦 **Necessário** publicar esse asset no Exchange **da sua organização**.

```xml
<dependency>
  <groupId>${orgId}</groupId> <!-- Substitua ${orgId} pelo GUID da sua organização -->
  <artifactId>api-gateway-http-policy-attributes-extension</artifactId>
  <version>1.0.0</version>
  <classifier>mule-plugin</classifier>
</dependency>
```

*Se o `groupId` não corresponder ao GUID da sua organização no Anypoint Platform,
o Maven não localizará a dependência no Exchange e a aplicação falhará.*

---

## ❓ FAQ <a id="faq"></a>

| Pergunta                                         | Resposta                                             |
| ------------------------------------------------ | ---------------------------------------------------- |
| Posso definir várias etapas `transform-request`? | Sim, são executadas na ordem declarada.              |
| Como alterar o limite de headers?                | Defina `http.headers.limit` no `mule-artifact.json`. |
| `REMOVE` sem `value` gera erro?                  | Apenas se a policy marcar o header como obrigatório. |

---

## 🗺️ Roadmap <a id="roadmap"></a>

* **1.1.0** – Suporte a cookies & query-params
* **1.2.0** – Testes MUnit + cobertura Jacoco
* **2.0.0** – Compatível Mule 5 (GA)

---

## 🐞 Troubleshooting <a id="troubleshooting"></a>

| Erro / Sintoma                          | Diagnóstico                                 | Correção                                         |
| --------------------------------------- | ------------------------------------------- | ------------------------------------------------ |
| `LIMIT_EXCEEDED`                        | Request contém cabeçalhos demais            | Aumente `http.headers.limit` ou filtre headers.  |
| Headers faltando na resposta            | `transform-response` executou antes de erro | Copie headers no `error-handler`.                |
| Metadados não carregam no Design Center | Faltou resolver                             | Verifique `HttpPolicyTransformMetadataResolver`. |

---

## 🤝 Contribuindo <a id="contribuindo"></a>

1. Fork → `git checkout -b feature/NOME`
2. `mvn clean test`
3. `git commit -m "feat: sua descrição"`
4. Abra Pull Request (padronize **Conventional Commits**).

---

## 👨‍💼 Desenvolvedor Responsável <a id="contato"></a>

**Autor:** Leonel Dorneles Porto
**Email:** [leoneldornelesporto@outlook.com.br](mailto:leoneldornelesporto@outlook.com.br)
**Organização:** Accenture

---

<p align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&size=22&pause=1000&color=00BFFF&center=true&vCenter=true&width=1000&lines=Transforma%C3%A7%C3%A3o+HTTP+pronta+para+qualquer+policy+%F0%9F%9A%80" />
</p>

---
