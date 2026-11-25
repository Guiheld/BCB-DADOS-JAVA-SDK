# Exemplos

Classes `main` demonstrando o uso da SDK. Ficam fora de `src/`, portanto **não** entram no artefato publicado.

## Como executar

Primeiro instale a SDK no repositório local e monte o classpath com as dependências:

```powershell
mvn -q -ntp install -DskipTests
mvn -q -ntp dependency:build-classpath "-Dmdep.outputFile=cp.txt"
```

Depois rode cada exemplo em modo arquivo-único (Java 17+). No Windows, o separador de classpath é `;`:

```powershell
java -cp "target/classes;$(Get-Content cp.txt)" examples/ExemploSgs.java
java -cp "target/classes;$(Get-Content cp.txt)" examples/ExemploCambio.java
java -cp "target/classes;$(Get-Content cp.txt)" examples/ExemploFocus.java
```

Em Linux/macOS, troque `;` por `:`.

| Exemplo | O que demonstra |
|---|---|
| `ExemploSgs.java` | Últimos valores de uma série e janelamento automático de série diária. |
| `ExemploCambio.java` | Cotação do dólar em um dia e listagem de moedas. |
| `ExemploFocus.java` | Consulta filtrada e ordenada às expectativas anuais de IPCA. |
| `ExemploSpringBoot.java` | Uso com o starter Spring Boot: injeção, cache e customizer. **Ilustrativo** (requer uma app Spring Boot). |

> Os exemplos acessam a API pública do Banco Central e, portanto, exigem conexão com a internet.

## Com Spring Boot

`ExemploSpringBoot.java` é **ilustrativo** — pressupõe uma aplicação Spring Boot com o
`bcb-dados-spring-boot-starter` e não roda pelo modo arquivo-único acima. Com o starter, o `BcbClient`
é injetado e configurado por `bcb.*` — em `application.properties` ou `application.yml`:

```yaml
bcb:
  max-tentativas: 5
  timeout-resposta: 45s
  vazao-por-segundo: 2.0
```

Cache, observabilidade, health check e testes estão documentados no
[README do starter](../bcb-dados-spring-boot-starter/README.md).
