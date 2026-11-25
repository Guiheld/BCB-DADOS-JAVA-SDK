# Changelog

Todas as mudanças notáveis deste projeto são documentadas neste arquivo.

O formato segue o [Keep a Changelog](https://keepachangelog.com/pt-BR/1.1.0/) e o projeto adota o
[Versionamento Semântico](https://semver.org/lang/pt-BR/).

## [Não lançado]

## [0.2.0] — 2026-11-25

### Adicionado

- `BcbClientConfig.httpClient(HttpClient)`: permite fornecer um `HttpClient` próprio ao transporte
  (proxy, autenticação, executor ou configuração corporativa). Quando ausente, a SDK mantém o cliente
  padrão.

### Alterado

- As listas devolvidas por todas as consultas (SGS, câmbio e Focus) agora são **imutáveis** e
  consistentes; tentativas de modificá-las lançam `UnsupportedOperationException`.

## [0.1.0] — 2026-11-24

### Adicionado

- Cliente `BcbClient` com configuração via `BcbClientConfig` (builder).
- Sub-API **SGS** (séries temporais) com consulta por intervalo, últimos valores e janelamento
  automático de séries diárias em janelas de até 10 anos, expostas como `Stream`.
- Sub-API **Câmbio/PTAX** com cotações de dólar e de outras moedas, por dia e por período, e lista
  de moedas.
- Sub-API **Expectativas/Focus** com consulta fluente a todos os EntitySets, filtros, ordenação,
  projeção e paginação automática.
- Transporte HTTP resiliente (novas tentativas com backoff exponencial e _jitter_, respeito a
  `Retry-After`, limitação de vazão) sobre o `HttpClient` do JDK.
- Hierarquia de exceções tipadas (`BcbException` e derivadas).
- Catálogo de séries comuns em `SerieSgs` e enums auxiliares.

[Não lançado]: https://github.com/Guiheld/BCB-DADOS-JAVA-SDK/compare/v0.2.0...HEAD
[0.2.0]: https://github.com/Guiheld/BCB-DADOS-JAVA-SDK/compare/v0.1.0...v0.2.0
[0.1.0]: https://github.com/Guiheld/BCB-DADOS-JAVA-SDK/releases/tag/v0.1.0
