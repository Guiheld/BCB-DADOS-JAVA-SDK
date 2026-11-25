# bcb-dados-spring-boot-starter

Auto-configuração **Spring Boot** para o [BCB-DADOS-JAVA-SDK](../README.md). Adicione a dependência e
injete um `BcbClient` pronto, configurado por `bcb.*` no `application.yml` — sem nenhum `@Bean` manual.

> Requer **Spring Boot 3.x** (baseline Java 17). Disponível a partir do release **0.3.0**.

## Sumário

- [Instalação](#instalação)
- [Começo rápido](#começo-rápido)
- [Configuração](#configuração)
- [Sobrescrevendo o cliente](#sobrescrevendo-o-cliente)
- [Ajuste programático](#ajuste-programático)
- [Cache com Spring](#cache-com-spring)
- [Observabilidade](#observabilidade)
- [Health check (Actuator)](#health-check-actuator)
- [Testes](#testes)
- [Como funciona](#como-funciona)

## Instalação

```xml
<dependency>
    <groupId>solutions.held</groupId>
    <artifactId>bcb-dados-spring-boot-starter</artifactId>
    <version>0.3.0</version>
</dependency>
```

O starter traz o core (`bcb-dados-java-sdk`) **transitivamente** — você declara só esta dependência.

## Começo rápido

```java
@Service
public class CambioService {

    private final BcbClient bcb;

    public CambioService(BcbClient bcb) { // injetado e configurado pelo starter
        this.bcb = bcb;
    }

    public Optional<BigDecimal> dolarVenda(LocalDate dia) {
        return bcb.cambio().cotacaoDolarDia(dia).map(CotacaoDolar::cotacaoVenda);
    }
}
```

## Configuração

Todas as propriedades são **opcionais**: o que você não definir mantém o padrão da SDK. O prefixo é
`bcb` e vale o _relaxed binding_ do Spring (kebab-case, em `.properties` ou `.yml`).

| Propriedade (`bcb.*`) | Tipo | Padrão | Descrição |
|---|---|---|---|
| `url-base-sgs` | String | endpoint oficial | Endereço base do SGS. |
| `url-base-ptax` | String | endpoint oficial | Endereço base do câmbio PTAX. |
| `url-base-expectativas` | String | endpoint oficial | Endereço base do Focus. |
| `timeout-conexao` | Duration | `10s` | Tempo limite de conexão. |
| `timeout-resposta` | Duration | `30s` | Tempo limite de resposta por requisição. |
| `max-tentativas` | int | `3` | Máximo de tentativas por requisição (a 1ª já conta). |
| `backoff-inicial` | Duration | `500ms` | Atraso inicial do backoff. |
| `backoff-maximo` | Duration | `10s` | Atraso máximo do backoff. |
| `multiplicador-backoff` | double | `2.0` | Multiplicador do backoff exponencial. |
| `vazao-por-segundo` | double | `3.0` | Vazão máxima client-side (≤ 0 desliga o limitador). |
| `janela-maxima-sgs` | Period | `10y` | Janela de fatiamento das séries diárias do SGS. |
| `tamanho-pagina-focus` | int | `1000` | Tamanho de página da paginação automática do Focus. |
| `user-agent` | String | `BCB-DADOS-JAVA-SDK/<versão>` | Identificação no cabeçalho `User-Agent`. |

Vale igualmente em `application.properties` ou `application.yml` — mesmo binding, você escolhe o formato.

`application.properties`:

```properties
bcb.max-tentativas=5
bcb.timeout-resposta=45s
bcb.backoff-inicial=500ms
bcb.vazao-por-segundo=2.0
bcb.janela-maxima-sgs=10y
```

`application.yml`:

```yaml
bcb:
  max-tentativas: 5
  timeout-resposta: 45s
  backoff-inicial: 500ms
  vazao-por-segundo: 2.0
  janela-maxima-sgs: 10y
```

Como é `@ConfigurationProperties`, você ganha **autocompletar e documentação** dessas chaves na IDE
(via `spring-boot-configuration-processor`), e pode sobrepor por _profile_ (`application-prod.yml`),
variável de ambiente (`BCB_MAX_TENTATIVAS`) ou argumento de linha de comando — como qualquer config do
Spring Boot.

## Sobrescrevendo o cliente

A auto-configuração usa `@ConditionalOnMissingBean`: se você declarar o seu próprio `BcbClient` (ou
`BcbClientConfig`), o starter **recua** e respeita o seu.

```java
@Bean
BcbClient bcbClient() {
    return BcbClient.comConfig(BcbClientConfig.builder().maxTentativas(1).build());
}
```

## Ajuste programático

Para o que não cabe em propriedades (um `HttpClient` próprio, um `ObjectMapper` custom), declare um
`BcbClientConfigCustomizer`. Ele roda **depois** das propriedades `bcb.*`, então tem a palavra final:

```java
@Bean
BcbClientConfigCustomizer bcbCustomizer() {
    return builder -> builder
            .httpClient(meuHttpClient)   // proxy, autenticação, executor, cliente instrumentado...
            .objectMapper(meuObjectMapper);
}
```

## Cache com Spring

O Spring Cache (`@EnableCaching` + `@Cacheable`) combina muito bem com a SDK, porque **boa parte dos
dados do BCB é imutável**: um ponto do SGS ou uma cotação PTAX de uma data passada e fechada nunca
muda. Cacheie essas consultas e você elimina chamadas repetidas à API.

Dependências (ex.: Caffeine):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>
<dependency>
    <groupId>com.github.ben-manes.caffeine</groupId>
    <artifactId>caffeine</artifactId>
</dependency>
```

```java
@Configuration
@EnableCaching
class CacheConfig {}

@Service
public class SelicService {

    private final BcbClient bcb;

    public SelicService(BcbClient bcb) {
        this.bcb = bcb;
    }

    // A chave DEVE conter todos os parâmetros que variam.
    @Cacheable(value = "sgs-serie", key = "#serie.name() + ':' + #inicio + ':' + #fim")
    public List<ValorSgs> serie(SerieSgs serie, LocalDate inicio, LocalDate fim) {
        return bcb.sgs().consultar(serie, inicio, fim);
    }
}
```

```yaml
spring:
  cache:
    type: caffeine
    caffeine:
      spec: maximumSize=1000,expireAfterWrite=6h
```

O que cachear — e por quanto tempo:

| Dado | Muda? | Estratégia de TTL |
|---|---|---|
| SGS/PTAX em datas passadas e fechadas | Não (imutável) | Longo (horas, dias) |
| Lista de moedas, catálogo de séries | Raramente | Longo |
| Focus (expectativas de mercado) | Revisado ~semanalmente | Curto (minutos/horas) |
| `ultimos(...)`, consultas incluindo o dia corrente | Sim | Curto ou **sem cache** |

> Dica: separe em _caches_ diferentes (`value = "..."`) os dados imutáveis dos voláteis, para aplicar
> TTLs distintos. Nunca cacheie com chave que ignore um parâmetro da consulta.

## Observabilidade

O HTTP é feito pelo `HttpClient` do JDK, que é o ponto natural de instrumentação:

- **Tracing automático**: com o _agente_ do OpenTelemetry, as requisições do `java.net.http.HttpClient`
  já são rastreadas **sem nenhuma alteração de código**.
- **Cliente instrumentado/customizado**: para proxy corporativo, autenticação, um executor dedicado ou
  um cliente embrulhado com sua própria telemetria, forneça-o via `BcbClientConfigCustomizer`
  (`builder.httpClient(...)`).

A resiliência (novas tentativas, respeito a `Retry-After`, limitação de vazão e o janelamento do SGS) é
interna à SDK; o `HttpClient` enxerga apenas as requisições HTTP efetivamente emitidas.

## Health check (Actuator)

Um `HealthIndicator` simples que confirma a conectividade com o BCB (exemplo):

```java
@Component
class BcbHealthIndicator implements HealthIndicator {

    private final BcbClient bcb;

    BcbHealthIndicator(BcbClient bcb) {
        this.bcb = bcb;
    }

    @Override
    public Health health() {
        try {
            bcb.sgs().ultimos(SerieSgs.SELIC_META, 1);
            return Health.up().build();
        } catch (BcbException e) {
            return Health.down(e).build();
        }
    }
}
```

> Como isso faz uma chamada real à API, use em _readiness_ pontual (não em um `/health` de alta
> frequência) ou combine com cache.

## Testes

Substitua o `BcbClient` auto-configurado por um dublê nos seus testes:

```java
@SpringBootTest
class MinhaAppTest {

    @MockBean
    BcbClient bcb; // substitui o bean auto-configurado

    @Test
    void ... () { /* when(bcb.sgs())... */ }
}
```

Para testar a própria auto-configuração, use `ApplicationContextRunner` (ver
`BcbAutoConfiguracaoTest` neste módulo).

## Como funciona

- `BcbProperties` (record `@ConfigurationProperties("bcb")`) liga as chaves `bcb.*`.
- `BcbAutoConfiguration` (`@AutoConfiguration`) monta um `BcbClientConfig` a partir das propriedades
  preenchidas, aplica os `BcbClientConfigCustomizer` e expõe `BcbClientConfig` e `BcbClient` — ambos com
  `@ConditionalOnMissingBean`.
- Registrada em `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
  (mecanismo do Spring Boot 3).
