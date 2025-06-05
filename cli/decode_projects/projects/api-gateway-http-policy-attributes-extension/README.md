---

# 🔌 API Gateway HTTP Attributes Extension - Extensão Customizada Mule

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:0C2340,100:00BFFF&height=220&section=header&text=Extens%C3%A3o%20de%20Atributos%20HTTP&fontSize=40&fontColor=ffffff&animation=fadeIn" alt="Extension Banner" />
</p>

<p align="center">
  <a href="https://docs.mulesoft.com/mule-sdk/latest/"> <img src="https://img.shields.io/badge/Mule%20SDK-M%C3%B3dulo%20de%20Extens%C3%A3o-003B71?logo=mulesoft" /></a>
  <a href="https://openjdk.org/projects/jdk/8/"> <img src="https://img.shields.io/badge/Java-8%20%2F%2011-FF6A00?logo=java" /></a>
  <a href="https://maven.apache.org/"> <img src="https://img.shields.io/badge/Maven-3.x-C71A36?logo=apache-maven" /></a>
</p>

---

## 📖 Visão Geral

Esta **extensão customizada do Mule** permite acesso detalhado aos **atributos da requisição HTTP** dentro de políticas personalizadas do API Gateway.

Ela encapsula atributos da requisição atual, como:

* `requestPath`, `method`, `scheme`, `host`, `port`
* Todos os headers, query parameters e path variables

Ideal para uso dentro de políticas personalizadas aplicadas via API Manager.

---

## 📂 Estrutura do Projeto

* **api-gateway-http-policy-attributes-extension**
  Contém a implementação em Java e configuração Maven.

| Arquivo / Pasta                       | Finalidade                                                     |
| ------------------------------------- | -------------------------------------------------------------- |
| `pom.xml`                             | Build do Maven                                                 |
| `HttpRequestAttributesWrapper.java`   | POJO Java com os dados extraídos da requisição                 |
| `HttpAttributesWrapperExtension.java` | Classe principal que expõe a operação `get-request-attributes` |

> 🧱 Desenvolvido com Mule SDK 1.1.x, compatível com Java 8 ou 11.

---

## ⚙️ Operação: `getRequestAttributes`

Esta é a operação principal da extensão, podendo ser usada em políticas como:

```xml
<http-attrs:get-request-attributes
  doc:name="Extrair informações da requisição"
  target="httpAttrs"/>
```

Após a extração, a variável `httpAttrs` conterá:

* `headers` (Map)
* `queryParams` (Map)
* `pathParams` (Map)
* `requestPath`, `method`, `scheme`, `host`, `port`

Acessos possíveis:

```xml
#[vars.httpAttrs.headers['x-api-key']]
#[vars.httpAttrs.queryParams['user']]
#[vars.httpAttrs.requestPath]
```

---

## 🧪 Exemplo de Uso na Policy (template.xml)

```xml
<flow name="validate-request-policy">
  <http-attrs:get-request-attributes target="httpAttrs"/>

  <logger level="INFO" message="Método → #[vars.httpAttrs.method]"/>
  <logger level="INFO" message="Header x-api-key → #[vars.httpAttrs.headers['x-api-key']]"/>
</flow>
```

---

## 🧰 Comandos Maven

```bash
# Instalação local
mvn clean install -DskipTests

# Deploy para o Exchange (exige settings.xml configurado)
mvn clean deploy -s .maven/settings.xml
```

> Certifique-se de alinhar a versão do `pom.xml` com o `mule-artifact.json`, se estiver usando em uma policy.

---

## 👨‍💼 Desenvolvedor Responsável

**Autor:** Leonel Dorneles Porto
**Email:** [leoneldornelesporto@outlook.com.br](mailto:leoneldornelesporto@outlook.com.br)
**Organização:** Accenture / Telefônica Vivo

---

<p align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&size=22&pause=1000&color=00BFFF&center=true&vCenter=true&width=1000&lines=Extens%C3%A3o+reutiliz%C3%A1vel+para+captura+de+atributos+HTTP+em+pol%C3%ADticas+personalizadas!"/>
</p>

---