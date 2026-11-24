# Como contribuir

Obrigado pelo interesse em contribuir com o **BCB-DADOS-JAVA-SDK**!

## Fluxo

1. Faça um _fork_ e crie um _branch_ a partir de `master`.
2. Garanta que o build passa: `mvn verify`.
3. Formate o código antes de commitar: `mvn spotless:apply`.
4. Abra um _pull request_ para `master` descrevendo a mudança.

## Convenções de código

- **Java 17**; **dependência de runtime única**: Jackson. HTTP apenas pelo `HttpClient` do JDK.
- Modelos e DTOs como `record`s imutáveis; datas em `LocalDate`/`LocalDateTime`; valores em `BigDecimal`.
- **Jackson mínimo**: use `@JsonProperty` apenas quando o nome em Java diferir do JSON.
- **Javadoc formal em pt-BR, em bloco**, com `@param`/`@return`. A tag `@author` vai **somente em
  tipos e `package-info`** (nunca em métodos); `@since dd/MM/yyyy` em tipos e métodos.
- Tipos explícitos (sem `var`), imports explícitos (sem _wildcard_), `System.Logger` para logs e
  **nenhum dado pessoal** em log.
- **Testes com JUnit 5** apenas, usando `com.sun.net.httpserver.HttpServer` do JDK como _stub_
  (sem WireMock, sem Mockito).
- Formatação por **Spotless + palantir-java-format** (`mvn spotless:apply`).

## Relato de problemas

Abra uma _issue_ descrevendo o comportamento esperado e o observado, com um exemplo mínimo quando possível.
