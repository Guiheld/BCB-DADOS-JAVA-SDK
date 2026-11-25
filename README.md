# BCB-DADOS-JAVA-SDK

[![Java](https://img.shields.io/badge/Java-17%2B-blue)](#requisitos)
[![License](https://img.shields.io/badge/license-Apache--2.0-green)](LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/solutions.held/bcb-dados-java-sdk)](https://central.sonatype.com/artifact/solutions.held/bcb-dados-java-sdk)
[![CI](https://github.com/Guiheld/BCB-DADOS-JAVA-SDK/actions/workflows/ci.yml/badge.svg?event=pull_request)](https://github.com/Guiheld/BCB-DADOS-JAVA-SDK/actions/workflows/ci.yml?query=event%3Apull_request)

SDK Java para as APIs de **dados econômicos** do Banco Central do Brasil: séries temporais (**SGS**),
câmbio (**PTAX**) e expectativas de mercado (**Focus**). Imutável, resiliente e com **dependência de
runtime única** (Jackson) — o HTTP é feito pelo `HttpClient` do próprio JDK.

API oficial do Banco Central: <https://dadosabertos.bcb.gov.br/>

## Sumário

- [Por que esta SDK](#por-que-esta-sdk)
- [Requisitos](#requisitos)
- [Instalação](#instalação)
- [Começo rápido](#começo-rápido)
- [Consultas disponíveis](#consultas-disponíveis)
- [Janelamento automático do SGS](#janelamento-automático-do-sgs)
- [Tratamento de erros](#tratamento-de-erros)
- [Configuração](#configuração)
- [Comportamentos conhecidos da API](#comportamentos-conhecidos-da-api)
- [Documentação](#documentação)
- [Contribuição](#contribuição)
- [Licença](#licença)

## Por que esta SDK

Consumir as APIs do Banco Central direto parece simples — até você bater nas armadilhas reais: a mesma
série falha com HTTP 406 se você pedir mais de 10 anos, o PTAX devolve lista vazia (não erro) quando a
data vai no formato "errado", e cada serviço reporta erro em um formato diferente. Esta SDK esconde
isso atrás de uma API tipada, imutável e previsível — você escreve menos código e erra menos.

| Dor ao consumir a API direta | O que a SDK faz por você |
|---|---|
| Série diária com intervalo > 10 anos → HTTP 406 | **Fatiamento automático** em janelas e concatenação transparente |
| PTAX exige data em `MM-dd-yyyy` (senão vem vazio em silêncio) | Você passa `LocalDate`; a conversão é interna |
| Erros em `text/plain`, JSON ou embrulhados em `/* */` | Normalizados em exceções tipadas (`BcbException` e derivadas) |
| EntitySets do Focus com nomes e capitalização irregulares | Métodos nomeados e mapeamento de campos já prontos |
| Paginação manual do Focus | `stream()` com paginação automática |
| `valor` e datas como texto | `BigDecimal`, `LocalDate` e `LocalDateTime` já tipados |

E as garantias de engenharia:

- **Dependência de runtime única**: apenas Jackson; o HTTP é o `HttpClient` do próprio JDK.
- **Imutável de ponta a ponta**: modelos em `record` e **todas as listas retornadas são imutáveis**.
- **Resiliência embutida**: novas tentativas com backoff exponencial e _jitter_, respeito a
  `Retry-After` e limitação de vazão client-side.
- **Extensível quando precisa**: forneça seu próprio `HttpClient` (proxy, autenticação, executor,
  configuração corporativa) via `BcbClientConfig.builder().httpClient(...)`.

## Requisitos

- Java 17 ou superior.

## Instalação

**Maven**

```xml
<dependency>
    <groupId>solutions.held</groupId>
    <artifactId>bcb-dados-java-sdk</artifactId>
    <version>0.1.0</version>
</dependency>
```

**Gradle (Kotlin DSL)**

```kotlin
implementation("solutions.held:bcb-dados-java-sdk:0.1.0")
```

## Começo rápido

```java
import java.time.LocalDate;
import solutions.held.bcb.BcbClient;
import solutions.held.bcb.model.enums.SerieSgs;

BcbClient bcb = BcbClient.criar();

// SGS: últimos valores da meta Selic
bcb.sgs().ultimos(SerieSgs.SELIC_META, 1)
        .forEach(v -> System.out.println(v.data() + " = " + v.valor()));

// Câmbio: cotação do dólar em um dia
bcb.cambio().cotacaoDolarDia(LocalDate.of(2026, 11, 19))
        .ifPresent(c -> System.out.println("Dólar venda: " + c.cotacaoVenda()));

// Focus: expectativas anuais de IPCA, mais recentes primeiro
bcb.focus().anuais().indicador("IPCA").ordenarPor("Data", true).top(5).buscar()
        .forEach(e -> System.out.println(e.dataReferencia() + ": " + e.mediana()));
```

## Consultas disponíveis

### SGS — `bcb.sgs()`

| Método | Descrição |
|---|---|
| `ultimos(serie, n)` | Os `n` valores mais recentes. |
| `consultar(serie, inicio, fim)` | Valores em um intervalo (`List`). |
| `stream(serie, inicio, fim)` | Valores em um intervalo (`Stream` preguiçoso). |

Séries de catálogo em `SerieSgs`:

| Enum | Código | Periodicidade |
|---|---|---|
| `SELIC_DIARIA` | 11 | diária |
| `SELIC_META` | 432 | diária |
| `DOLAR_PTAX_VENDA` | 1 | diária |
| `IPCA` | 433 | mensal |
| `CDI` | 12 | diária |
| `IGP_M` | 189 | mensal |

Há sobrecargas recebendo `(int codigo, Periodicidade periodicidade, ...)` para séries fora do catálogo.

### Câmbio/PTAX — `bcb.cambio()`

| Método | Descrição |
|---|---|
| `cotacaoDolarDia(data)` | Cotação do dólar no dia (`Optional`). |
| `cotacaoDolarPeriodo(inicial, finalCotacao)` | Cotações do dólar no período. |
| `cotacaoMoedaDia(moeda, data)` | Todos os boletins de uma moeda no dia. |
| `cotacaoMoedaDiaFechamento(moeda, data)` | Apenas o boletim de fechamento PTAX. |
| `cotacaoMoedaPeriodo(moeda, inicial, finalCotacao)` | Cotações de uma moeda no período. |
| `moedas()` | Moedas disponíveis. |

### Expectativas/Focus — `bcb.focus()`

Cada método devolve uma `ConsultaFocus` fluente (`indicador`, `dataReferencia`, `desde`, `ate`,
`filtro`, `ordenarPor`, `top`, `selecionar`) finalizada com `buscar()` (`List`) ou `stream()`
(paginação automática):

`anuais()`, `mensais()`, `trimestrais()`, `inflacao12Meses()`, `inflacao24Meses()`, `selic()`,
`top5Anuais()`, `top5Mensais()`, `top5Trimestral()`, `top5Selic()`, `top5Inflacao12Meses()`,
`top5Inflacao24Meses()`, `datasReferencia()`.

## Janelamento automático do SGS

Séries **diárias** aceitam, por consulta, uma janela de no máximo **10 anos** (do contrário a API
responde HTTP 406). A SDK fatia o período automaticamente em janelas de até 10 anos e concatena os
resultados — você consulta qualquer intervalo sem se preocupar com o limite:

```java
long dias = bcb.sgs()
        .stream(SerieSgs.SELIC_DIARIA, LocalDate.of(2000, 1, 1), LocalDate.now())
        .count();
```

Séries mensais são isentas do limite e consultadas em uma única chamada.

## Tratamento de erros

Todas as exceções derivam de `BcbException` (não checada).

| Exceção | Quando ocorre |
|---|---|
| `BcbClientException` | Erro de cliente (HTTP 4xx), como código de série inexistente. |
| `BcbRateLimitException` | Excesso de requisições (HTTP 429); expõe o `Retry-After`. |
| `BcbLimiteJanelaException` | Série diária consultada com janela maior que 10 anos (HTTP 406). |
| `BcbServerException` | Erro de servidor (HTTP 5xx), após esgotar as tentativas. |
| `BcbSerializationException` | Falha ao desserializar a resposta. |
| `BcbException` | Falha de comunicação/rede ou erro genérico. |

```java
try {
    bcb.sgs().ultimos(SerieSgs.IPCA, 12);
} catch (BcbRateLimitException e) {
    e.tempoEsperaSugerido().ifPresent(d -> System.out.println("Aguardar " + d));
} catch (BcbException e) {
    System.err.println("Falha: " + e.getMessage());
}
```

## Configuração

```java
BcbClientConfig config = BcbClientConfig.builder()
        .maxTentativas(5)
        .timeoutResposta(Duration.ofSeconds(45))
        .vazaoPorSegundo(2.0)
        .build();

BcbClient bcb = BcbClient.comConfig(config);
```

Principais parâmetros: `urlBaseSgs`, `urlBasePtax`, `urlBaseExpectativas`, `timeoutConexao`,
`timeoutResposta`, `maxTentativas`, `backoffInicial`, `backoffMaximo`, `multiplicadorBackoff`,
`vazaoPorSegundo`, `janelaMaximaSgs`, `tamanhoPaginaFocus`, `userAgent`, `objectMapper`.

## Comportamentos conhecidos da API

- **SGS — valores são texto**: o campo `valor` vem como texto com ponto decimal e pode ser negativo
  (séries de variação). A SDK converte para `BigDecimal`.
- **SGS — meta Selic (432) é diária**: é entregue como série diária preenchida (um registro por dia,
  inclusive fins de semana) com datas até a próxima reunião do Copom; está sujeita ao limite de 10 anos.
- **PTAX — formato de data**: a API usa `MM-dd-yyyy`; a SDK recebe `LocalDate` e converte internamente.
- **PTAX — dia sem cotação**: fins de semana, feriados e datas futuras devolvem resultado vazio, não erro.
- **PTAX — `dataHoraCotacao`**: é um texto `yyyy-MM-dd HH:mm:ss.SSSSSS` no horário de Brasília (sem fuso);
  a SDK o converte para `LocalDateTime`.
- **Focus — nomes e campos irregulares**: nomes de EntitySet e capitalização de campos variam; a SDK já
  mapeia cada formato (inclusive o Top 5 Selic, com campos em caixa baixa e `coeficienteVariacao`).

## Documentação

- **Wiki**: <https://github.com/Guiheld/BCB-DADOS-JAVA-SDK/wiki>
- **Javadoc**: publicado junto ao artefato no Maven Central.
- **Exemplos**: pasta [`examples/`](examples/).

## Contribuição

Veja [CONTRIBUTING.md](CONTRIBUTING.md). Em resumo: `mvn verify` deve passar e
`mvn spotless:apply` formata o código.

## Licença

Distribuído sob a licença [Apache-2.0](LICENSE).

---

> Projeto independente, **sem afiliação** com o Banco Central do Brasil. "Banco Central do Brasil",
> "SGS", "PTAX" e "Focus" pertencem aos seus respectivos detentores.
