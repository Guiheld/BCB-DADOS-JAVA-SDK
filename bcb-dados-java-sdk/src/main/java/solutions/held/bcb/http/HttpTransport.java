package solutions.held.bcb.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import solutions.held.bcb.BcbClientConfig;
import solutions.held.bcb.exception.*;
import solutions.held.bcb.model.RespostaOData;

/**
 * Transporte HTTP da SDK: monta as requisições GET às APIs do Banco Central, aplica limitação de
 * vazão e novas tentativas, traduz os códigos de status em exceções tipadas e desserializa as
 * respostas.
 *
 * <p>
 *     Usa exclusivamente o {@link HttpClient} do JDK, sem dependências externas de rede. É de uso
 *     interno da SDK e seguro para uso concorrente. Trata as particularidades observadas nas APIs:
 *     o SGS devolve erro em {@code text/plain} (404) ou JSON (406), enquanto os serviços Olinda
 *     embrulham o JSON de erro em comentário {@code /* ... *}{@code /}.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class HttpTransport {

    private static final System.Logger LOGGER = System.getLogger(HttpTransport.class.getName());

    private final BcbClientConfig config;
    private final HttpClient httpClient;
    private final Throttler throttler;
    private final ObjectMapper objectMapper;

    /**
     * Cria o transporte a partir da configuração informada.
     *
     * <p>
     *     Usa o {@link HttpClient} fornecido em {@link BcbClientConfig#httpClient()} quando presente;
     *     caso contrário, constrói um cliente padrão com o tempo limite de conexão da configuração e
     *     política de redirecionamento {@code NORMAL}.
     * </p>
     *
     * @param config configuração do cliente
     * @since 24/11/2026
     */
    public HttpTransport(BcbClientConfig config) {
        this.config = config;
        this.objectMapper = config.objectMapper();
        this.throttler = new Throttler(config.vazaoPorSegundo());
        this.httpClient = config.httpClient()
                .orElseGet(() -> HttpClient.newBuilder()
                        .connectTimeout(config.timeoutConexao())
                        .followRedirects(HttpClient.Redirect.NORMAL)
                        .build());
    }

    /**
     * Consulta um recurso do SGS que devolve um array JSON e o desserializa em uma lista.
     *
     * @param <T> tipo dos itens esperados
     * @param url URL completa do recurso
     * @param tipoItem classe dos itens a desserializar
     * @return a lista imutável desserializada, ou lista vazia quando a resposta é vazia
     * @since 24/11/2026
     */
    public <T> List<T> obterListaSgs(String url, Class<T> tipoItem) {
        String corpo = this.executar(this.montar(url));
        if (corpo == null || corpo.isBlank()) return List.of();

        try {
            return List.copyOf(this.objectMapper.readValue(
                    corpo, this.objectMapper.getTypeFactory().constructCollectionType(List.class, tipoItem)));
        } catch (java.io.IOException e) {
            throw new BcbSerializationException("Falha ao desserializar a resposta do SGS.", e);
        }
    }

    /**
     * Consulta um recurso OData que devolve o envelope {@code {"value":[...]}} e extrai os registros.
     *
     * @param <T> tipo dos itens esperados
     * @param url URL completa do recurso OData
     * @param tipoItem classe dos itens a desserializar
     * @return a lista imutável de registros, ou lista vazia quando não há dados
     * @since 24/11/2026
     */
    public <T> List<T> obterListaOData(String url, Class<T> tipoItem) {
        String corpo = this.executar(this.montar(url));
        if (corpo == null || corpo.isBlank()) return List.of();

        try {
            RespostaOData<T> resposta = this.objectMapper.readValue(
                    corpo, this.objectMapper.getTypeFactory().constructParametricType(RespostaOData.class, tipoItem));
            return resposta.value() == null ? List.of() : List.copyOf(resposta.value());
        } catch (java.io.IOException e) {
            throw new BcbSerializationException("Falha ao desserializar a resposta OData do Banco Central.", e);
        }
    }

    private HttpRequest montar(String url) {
        return HttpRequest.newBuilder(URI.create(url))
                .timeout(this.config.timeoutResposta())
                .header("Accept", "application/json")
                .header("User-Agent", this.config.userAgent())
                .GET()
                .build();
    }

    private String executar(HttpRequest requisicao) {
        Integer maxTentativas = this.config.maxTentativas();
        BcbException ultimoErro = null;

        for (Integer tentativa = 1; tentativa <= maxTentativas; tentativa++) {
            this.throttler.adquirir();
            HttpResponse<String> resposta;

            try {
                resposta = this.httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            } catch (java.io.IOException e) {
                ultimoErro = new BcbException("Falha de comunicação com a API do Banco Central.", e);
                if (tentativa < maxTentativas) {
                    this.aguardarBackoff(tentativa, null);
                    continue;
                }
                throw ultimoErro;
            } catch (java.lang.InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BcbException("Consulta à API do Banco Central interrompida.", e);
            }

            int status = resposta.statusCode();
            String corpo = resposta.body();

            if (status == 204) return null;
            if (status >= 200 && status < 300) return corpo;
            if (status == 429) {
                java.time.Duration tempoEspera = this.lerRetryAfter(resposta).orElse(null);
                if (tentativa < maxTentativas) {
                    this.aguardarBackoff(tentativa, tempoEspera);
                    continue;
                }
                throw new BcbRateLimitException(
                        "Limite de requisições do Banco Central excedido (HTTP 429).", status, corpo, tempoEspera);
            }
            if (status == 406) {
                throw new BcbLimiteJanelaException(
                        "Janela de consulta excedida: séries diárias do SGS aceitam no máximo dez anos por consulta (HTTP 406).",
                        status,
                        corpo,
                        this.interpretarErro(corpo));
            }
            if (status >= 500) {
                if (tentativa < maxTentativas) {
                    this.aguardarBackoff(tentativa, null);
                    continue;
                }
                throw new BcbServerException(
                        "Erro no servidor do Banco Central (HTTP " + status + ").",
                        status,
                        corpo,
                        this.interpretarErro(corpo));
            }
            throw new BcbClientException(
                    "Requisição rejeitada pelo Banco Central (HTTP " + status + ").",
                    status,
                    corpo,
                    this.interpretarErro(corpo));
        }
        throw ultimoErro != null
                ? ultimoErro
                : new BcbException("Falha ao consultar o Banco Central após " + maxTentativas + " tentativa(s).");
    }

    private void aguardarBackoff(Integer tentativa, java.time.Duration tempoSugerido) {
        java.time.Duration espera;
        if (tempoSugerido != null) {
            espera = tempoSugerido;
        } else {
            long millis = Math.min(
                    (long) (this.config.backoffInicial().toMillis()
                            * Math.pow(this.config.multiplicadorBackoff(), tentativa - 1)),
                    this.config.backoffMaximo().toMillis());
            espera = java.time.Duration.ofMillis(millis
                    + ThreadLocalRandom.current().nextLong(0, Math.max(1L, millis / 4 + 1)));
        }
        LOGGER.log(
                System.Logger.Level.DEBUG,
                "Aguardando {0} ms antes de nova tentativa ao Banco Central (tentativa {1}).",
                espera.toMillis(),
                tentativa);
        try {
            Thread.sleep(espera.toMillis());
        } catch (java.lang.InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BcbException("Espera entre tentativas ao Banco Central interrompida.", e);
        }
    }

    private Optional<Duration> lerRetryAfter(HttpResponse<String> resposta) {
        return resposta.headers().firstValue("Retry-After").flatMap(HttpTransport::interpretarRetryAfter);
    }

    private static Optional<Duration> interpretarRetryAfter(String valor) {
        try {
            return Optional.of(Duration.ofSeconds(Long.parseLong(valor.trim())));
        } catch (java.lang.NumberFormatException e) {
            return Optional.empty();
        }
    }

    private ErroBcb interpretarErro(String corpo) {
        if (corpo == null || corpo.isBlank()) return null;

        String texto = corpo.strip();
        if (texto.startsWith("/*") && texto.endsWith("*/"))
            texto = texto.substring(2, texto.length() - 2).strip();
        if (texto.startsWith("{") || texto.startsWith("[")) {
            try {
                return this.objectMapper.readValue(texto, ErroBcb.class);
            } catch (java.io.IOException e) {
                LOGGER.log(
                        System.Logger.Level.DEBUG,
                        "Corpo de erro do Banco Central não é JSON reconhecido; preservado como texto bruto.");
            }
        }
        return new ErroBcb(null, null, null, null, null, corpo.strip());
    }
}
