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

> Os exemplos acessam a API pública do Banco Central e, portanto, exigem conexão com a internet.
