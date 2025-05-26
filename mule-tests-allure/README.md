---

# 🧪 Mule Tests Allure Constants – Biblioteca de Constantes para Anotações Allure

<p align="center">
  <img src="https://capsule-render.vercel.app/api?type=waving&color=0:0C2340,100:00BFFF&height=220&section=header&text=Mule%20EE%20Allure%20Tests&fontSize=38&fontColor=ffffff&animation=fadeIn" alt="Banner" />
</p>

<p align="center">
  <a href="https://docs.qameta.io/allure/"><img src="https://img.shields.io/badge/Allure-Framework-ED3B5B?logo=allure" /></a>
  <a href="https://openjdk.org/projects/jdk/8/"><img src="https://img.shields.io/badge/Java-8%20%2F%2011-FF6A00?logo=java" /></a>
  <a href="https://maven.apache.org/"><img src="https://img.shields.io/badge/Maven-3.x-C71A36?logo=apache-maven" /></a>
</p>

---

## 📖 Visão Geral

A **Mule Tests Allure Constants** fornece um único artefato **JAR** contendo *strings* de **feature** e **story** reutilizáveis para serem usadas como anotações `@Feature` e `@Story` do **Allure** em suítes de teste de **Mule 4 EE**.

> 🔎 Motivação
> • Padronizar nomes de funcionalidades/fluxos de teste em múltiplos projetos.
> • Evitar *hard-coding* repetitivo nos testes.
> • Aumentar a qualidade dos relatórios Allure com categorização consistente.

---

## 📂 Estrutura do Projeto

| Arquivo / Pasta                                               | Propósito                                                                     |
| ------------------------------------------------------------- | ----------------------------------------------------------------------------- |
| `pom.xml`                                                     | Configuração Maven (packaging `jar`)                                          |
| `src/main/java/com/mulesoft/test/allure/AllureConstants.java` | Interface raiz com sub-interfaces estáticas para cada **feature** / **story** |
| *(gerados)* `target/*.jar`                                    | Artefato compilado pronto para publicar / consumir                            |

### Highlights da Classe `AllureConstants`

* **ServerPluginsFeature** – *Server plugins*
* **FipsFeatures** – *Java 11* / *Java 8* ➜ *Mule runtime in FIPS mode*
* **Profiling**, **KryoSerializerFeature**, **CacheFeature**
* **BatchFeature** ➜ *EL functions*, *Job management*
* **ClusteringFeature** ➜ *Distributed map*, *Error handling* …
* **CompatibilityFeature** ➜ *HTTP Transport*, *JMS Transport*, *Spring Extras*
* **EeComponentsFeature** ➜ *Dynamic Evaluate*, *Transform*

Todas as constantes são `public static final String`, facilitando a referência sem *imports* extras.

---

## 🛠️ Como Usar

```java
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static com.mulesoft.test.allure.AllureConstants.*;

@Feature(BatchFeature.BATCH)
@Story(BatchFeature.BatchStories.EL_FUNCTIONS)
public class BatchFunctionsTest {

  @Test
  public void shouldExecuteElFunctions() {
    // ...
  }
}
```

* **feature** e **story** são resolvidos em *build-time*, evitando erros de digitação.
* Atualizar uma *string* na biblioteca reflete em todos os projetos que a consomem.

---

## 🧰 Comandos Maven

```bash
# Build local
mvn clean install -DskipTests

# Publicar no Anypoint Exchange da sua organização
mvn clean deploy -s .maven/settings.xml
```

> 📝 Antes de fazer **deploy**, garanta que o arquivo `settings.xml` contém a *Connected App* com permissão **Design Center / Exchange Contributor**.

---

## 🏷️ Dependência Maven

```xml
<dependency>
  <groupId>${org.id}</groupId> <!-- substitua pelo seu Group Id -->
  <artifactId>mule-tests-allure</artifactId>
  <version>1.0.0</version>
</dependency>
```

---

## 🚦 Roadmap

* **1.1.0** – Adicionar constantes para módulos *DataWeave* & *Validation*.
* **1.2.0** – Suporte a etiquetas dinâmicas via **Enums**.
* **2.0.0** – Migração para **JDK 17** e Allure 3.

---

## ❓ FAQ

| Pergunta                    | Resposta                                                                   |
| --------------------------- | -------------------------------------------------------------------------- |
| Posso alterar as *strings*? | Sim, mas gere uma nova versão; consumidores devem atualizar a dependência. |
| Preciso do jar em runtime?  | Apenas na fase **test**; use `<scope>test</scope>` se desejar.             |
| Funciona com JUnit 5?       | Sim, desde que utilize as anotações Allure adequadas.                      |

---

## 👨‍💼 Desenvolvedor Responsável

**Autor:** Leonel Dorneles Porto  
**Email:** [leoneldornelesporto@outlook.com.br](mailto:leoneldornelesporto@outlook.com.br)  
**Organização:** Accenture

---

<p align="center">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&size=22&pause=1000&color=00BFFF&center=true&vCenter=true&width=1500&lines=Relat%C3%B3rios+Allure+mais+limpos+e+padronizados+para+seus+tests+Mule+EE!" />
</p>

---
