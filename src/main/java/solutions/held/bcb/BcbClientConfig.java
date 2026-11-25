package solutions.held.bcb;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.time.Duration;
import java.time.Period;
import java.util.Optional;
import java.util.Properties;

/**
 * Configuração imutável do cliente da SDK de dados econômicos do Banco Central.
 *
 * <p>
 *     Reúne os endereços base dos três serviços (SGS, câmbio/PTAX e Expectativas/Focus), os tempos
 *     limite, a política de novas tentativas, a vazão máxima de requisições, a janela máxima do SGS,
 *     o tamanho de página do Focus e o {@link ObjectMapper} usado na desserialização. É criada pelo
 *     {@link Builder} e usada por {@link BcbClient}.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class BcbClientConfig {

    /** Endereço base padrão do serviço SGS (séries temporais). */
    public static final String URL_BASE_SGS_PADRAO = "https://api.bcb.gov.br/dados/serie";

    /** Endereço base padrão do serviço de câmbio PTAX (OData). */
    public static final String URL_BASE_PTAX_PADRAO = "https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata";

    /** Endereço base padrão do serviço de Expectativas/Focus (OData). */
    public static final String URL_BASE_EXPECTATIVAS_PADRAO =
            "https://olinda.bcb.gov.br/olinda/servico/Expectativas/versao/v1/odata";

    /**
     * Identificação padrão enviada no cabeçalho {@code User-Agent}, no formato
     * {@code BCB-DADOS-JAVA-SDK/<versão>}. A versão é derivada do {@code project.version} do POM
     * (lida de um recurso de build filtrado pelo Maven); vale {@code dev} quando indisponível.
     */
    public static final String USER_AGENT_PADRAO = "BCB-DADOS-JAVA-SDK/" + resolverVersao();

    /** Vazão padrão de requisições por segundo aplicada pelo limitador client-side. */
    public static final double VAZAO_PADRAO_POR_SEGUNDO = 3.0;

    private final String urlBaseSgs;
    private final String urlBasePtax;
    private final String urlBaseExpectativas;
    private final Duration timeoutConexao;
    private final Duration timeoutResposta;
    private final Integer maxTentativas;
    private final Duration backoffInicial;
    private final Duration backoffMaximo;
    private final Double multiplicadorBackoff;
    private final Double vazaoPorSegundo;
    private final Period janelaMaximaSgs;
    private final Integer tamanhoPaginaFocus;
    private final String userAgent;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    private BcbClientConfig(Builder builder) {
        this.urlBaseSgs = builder.urlBaseSgs;
        this.urlBasePtax = builder.urlBasePtax;
        this.urlBaseExpectativas = builder.urlBaseExpectativas;
        this.timeoutConexao = builder.timeoutConexao;
        this.timeoutResposta = builder.timeoutResposta;
        this.maxTentativas = builder.maxTentativas;
        this.backoffInicial = builder.backoffInicial;
        this.backoffMaximo = builder.backoffMaximo;
        this.multiplicadorBackoff = builder.multiplicadorBackoff;
        this.vazaoPorSegundo = builder.vazaoPorSegundo;
        this.janelaMaximaSgs = builder.janelaMaximaSgs;
        this.tamanhoPaginaFocus = builder.tamanhoPaginaFocus;
        this.userAgent = builder.userAgent;
        this.objectMapper = builder.objectMapper == null ? mapperPadrao() : builder.objectMapper;
        this.httpClient = builder.httpClient;
    }

    /**
     * Cria um construtor com os valores padrão.
     *
     * @return um novo {@link Builder}
     * @since 24/11/2026
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Cria a configuração com todos os valores padrão.
     *
     * @return a configuração padrão
     * @since 24/11/2026
     */
    public static BcbClientConfig padrao() {
        return builder().build();
    }

    /**
     * Cria o {@link ObjectMapper} padrão da SDK, tolerante a campos desconhecidos e configurado para
     * datas do pacote {@code java.time}.
     *
     * @return o {@link ObjectMapper} padrão
     * @since 24/11/2026
     */
    public static ObjectMapper mapperPadrao() {
        return JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL)
                .build();
    }

    /**
     * Lê a versão da SDK a partir do recurso de build filtrado pelo Maven.
     *
     * @return a versão declarada no {@code project.version} do POM, ou {@code dev} quando indisponível
     * @since 25/11/2026
     */
    private static String resolverVersao() {
        try (InputStream entrada = BcbClientConfig.class.getResourceAsStream("/bcb-dados-java-sdk.properties")) {
            if (entrada != null) {
                Properties propriedades = new Properties();
                propriedades.load(entrada);
                String versao = propriedades.getProperty("version");
                if (versao != null && !versao.isBlank() && !versao.contains("${")) {
                    return versao.strip();
                }
            }
        } catch (IOException e) {
            // Recurso ausente ou ilegível: usa o rótulo de desenvolvimento abaixo.
        }
        return "dev";
    }

    /**
     * Devolve o endereço base do serviço SGS.
     *
     * @return o endereço base do SGS
     * @since 24/11/2026
     */
    public String urlBaseSgs() {
        return this.urlBaseSgs;
    }

    /**
     * Devolve o endereço base do serviço de câmbio PTAX.
     *
     * @return o endereço base do PTAX
     * @since 24/11/2026
     */
    public String urlBasePtax() {
        return this.urlBasePtax;
    }

    /**
     * Devolve o endereço base do serviço de Expectativas/Focus.
     *
     * @return o endereço base do Focus
     * @since 24/11/2026
     */
    public String urlBaseExpectativas() {
        return this.urlBaseExpectativas;
    }

    /**
     * Devolve o tempo limite de estabelecimento de conexão.
     *
     * @return o tempo limite de conexão
     * @since 24/11/2026
     */
    public Duration timeoutConexao() {
        return this.timeoutConexao;
    }

    /**
     * Devolve o tempo limite de resposta de cada requisição.
     *
     * @return o tempo limite de resposta
     * @since 24/11/2026
     */
    public Duration timeoutResposta() {
        return this.timeoutResposta;
    }

    /**
     * Devolve o número máximo de tentativas por requisição (a primeira já conta).
     *
     * @return o número máximo de tentativas
     * @since 24/11/2026
     */
    public Integer maxTentativas() {
        return this.maxTentativas;
    }

    /**
     * Devolve o atraso inicial do backoff entre tentativas.
     *
     * @return o atraso inicial do backoff
     * @since 24/11/2026
     */
    public Duration backoffInicial() {
        return this.backoffInicial;
    }

    /**
     * Devolve o atraso máximo do backoff entre tentativas.
     *
     * @return o atraso máximo do backoff
     * @since 24/11/2026
     */
    public Duration backoffMaximo() {
        return this.backoffMaximo;
    }

    /**
     * Devolve o multiplicador do backoff exponencial.
     *
     * @return o multiplicador do backoff
     * @since 24/11/2026
     */
    public Double multiplicadorBackoff() {
        return this.multiplicadorBackoff;
    }

    /**
     * Devolve a vazão máxima de requisições por segundo do limitador client-side.
     *
     * @return a vazão máxima por segundo
     * @since 24/11/2026
     */
    public Double vazaoPorSegundo() {
        return this.vazaoPorSegundo;
    }

    /**
     * Devolve a janela máxima de consulta usada no fatiamento das séries diárias do SGS.
     *
     * @return a janela máxima do SGS
     * @since 24/11/2026
     */
    public Period janelaMaximaSgs() {
        return this.janelaMaximaSgs;
    }

    /**
     * Devolve o tamanho de página usado na paginação automática do Focus.
     *
     * @return o tamanho de página do Focus
     * @since 24/11/2026
     */
    public Integer tamanhoPaginaFocus() {
        return this.tamanhoPaginaFocus;
    }

    /**
     * Devolve a identificação enviada no cabeçalho {@code User-Agent}.
     *
     * @return o valor do {@code User-Agent}
     * @since 24/11/2026
     */
    public String userAgent() {
        return this.userAgent;
    }

    /**
     * Devolve o {@link ObjectMapper} usado na (de)serialização.
     *
     * @return o {@link ObjectMapper} configurado
     * @since 24/11/2026
     */
    public ObjectMapper objectMapper() {
        return this.objectMapper;
    }

    /**
     * Devolve o {@link HttpClient} personalizado a ser usado pelo transporte, quando informado.
     *
     * <p>
     *     Quando presente, o transporte usa este cliente como está — nesse caso o
     *     {@link #timeoutConexao()} e a política de redirecionamento passam a ser os do cliente
     *     fornecido, não os da configuração. O {@link #timeoutResposta()} continua aplicado por
     *     requisição. Quando ausente, o transporte constrói um cliente padrão.
     * </p>
     *
     * @return o {@link HttpClient} personalizado, ou vazio para usar o padrão
     * @since 24/11/2026
     */
    public Optional<HttpClient> httpClient() {
        return Optional.ofNullable(this.httpClient);
    }

    /**
     * Construtor fluente de {@link BcbClientConfig}.
     *
     * @author Guilherme A. Held
     * @since 24/11/2026
     */
    public static final class Builder {

        private String urlBaseSgs = URL_BASE_SGS_PADRAO;
        private String urlBasePtax = URL_BASE_PTAX_PADRAO;
        private String urlBaseExpectativas = URL_BASE_EXPECTATIVAS_PADRAO;
        private Duration timeoutConexao = Duration.ofSeconds(10);
        private Duration timeoutResposta = Duration.ofSeconds(30);
        private Integer maxTentativas = 3;
        private Duration backoffInicial = Duration.ofMillis(500);
        private Duration backoffMaximo = Duration.ofSeconds(10);
        private Double multiplicadorBackoff = 2.0;
        private Double vazaoPorSegundo = VAZAO_PADRAO_POR_SEGUNDO;
        private Period janelaMaximaSgs = Period.ofYears(10);
        private Integer tamanhoPaginaFocus = 1000;
        private String userAgent = USER_AGENT_PADRAO;
        private ObjectMapper objectMapper;
        private HttpClient httpClient;

        private Builder() {}

        /**
         * Define o endereço base do serviço SGS.
         *
         * @param urlBaseSgs endereço base do SGS
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder urlBaseSgs(String urlBaseSgs) {
            this.urlBaseSgs = urlBaseSgs;
            return this;
        }

        /**
         * Define o endereço base do serviço de câmbio PTAX.
         *
         * @param urlBasePtax endereço base do PTAX
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder urlBasePtax(String urlBasePtax) {
            this.urlBasePtax = urlBasePtax;
            return this;
        }

        /**
         * Define o endereço base do serviço de Expectativas/Focus.
         *
         * @param urlBaseExpectativas endereço base do Focus
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder urlBaseExpectativas(String urlBaseExpectativas) {
            this.urlBaseExpectativas = urlBaseExpectativas;
            return this;
        }

        /**
         * Define o tempo limite de conexão.
         *
         * @param timeoutConexao tempo limite de conexão
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder timeoutConexao(Duration timeoutConexao) {
            this.timeoutConexao = timeoutConexao;
            return this;
        }

        /**
         * Define o tempo limite de resposta.
         *
         * @param timeoutResposta tempo limite de resposta
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder timeoutResposta(Duration timeoutResposta) {
            this.timeoutResposta = timeoutResposta;
            return this;
        }

        /**
         * Define o número máximo de tentativas por requisição.
         *
         * @param maxTentativas número máximo de tentativas (mínimo 1)
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder maxTentativas(Integer maxTentativas) {
            this.maxTentativas = maxTentativas;
            return this;
        }

        /**
         * Define o atraso inicial do backoff.
         *
         * @param backoffInicial atraso inicial
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder backoffInicial(Duration backoffInicial) {
            this.backoffInicial = backoffInicial;
            return this;
        }

        /**
         * Define o atraso máximo do backoff.
         *
         * @param backoffMaximo atraso máximo
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder backoffMaximo(Duration backoffMaximo) {
            this.backoffMaximo = backoffMaximo;
            return this;
        }

        /**
         * Define o multiplicador do backoff exponencial.
         *
         * @param multiplicadorBackoff multiplicador (mínimo 1.0)
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder multiplicadorBackoff(Double multiplicadorBackoff) {
            this.multiplicadorBackoff = multiplicadorBackoff;
            return this;
        }

        /**
         * Define a vazão máxima de requisições por segundo (0 ou negativo desliga o limitador).
         *
         * @param vazaoPorSegundo vazão máxima por segundo
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder vazaoPorSegundo(Double vazaoPorSegundo) {
            this.vazaoPorSegundo = vazaoPorSegundo;
            return this;
        }

        /**
         * Define a janela máxima de consulta das séries diárias do SGS.
         *
         * @param janelaMaximaSgs janela máxima (no máximo dez anos)
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder janelaMaximaSgs(Period janelaMaximaSgs) {
            this.janelaMaximaSgs = janelaMaximaSgs;
            return this;
        }

        /**
         * Define o tamanho de página da paginação automática do Focus.
         *
         * @param tamanhoPaginaFocus tamanho de página (mínimo 1)
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder tamanhoPaginaFocus(Integer tamanhoPaginaFocus) {
            this.tamanhoPaginaFocus = tamanhoPaginaFocus;
            return this;
        }

        /**
         * Define a identificação do cabeçalho {@code User-Agent}.
         *
         * @param userAgent valor do {@code User-Agent}
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        /**
         * Define o {@link ObjectMapper} a ser usado. Quando ausente, usa {@link #mapperPadrao()}.
         *
         * @param objectMapper mapeador a ser usado
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder objectMapper(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
            return this;
        }

        /**
         * Define um {@link HttpClient} próprio a ser usado pelo transporte (por exemplo, com proxy,
         * autenticação, executor ou configuração corporativa). Quando ausente, a SDK constrói um
         * cliente padrão a partir do {@link #timeoutConexao(Duration)}.
         *
         * <p>
         *     Ao fornecer um cliente, o tempo limite de conexão e a política de redirecionamento
         *     passam a ser os definidos nesse cliente; o tempo limite de resposta continua aplicado
         *     por requisição pela SDK.
         * </p>
         *
         * @param httpClient cliente HTTP a ser usado
         * @return este construtor
         * @since 24/11/2026
         */
        public Builder httpClient(HttpClient httpClient) {
            this.httpClient = httpClient;
            return this;
        }

        /**
         * Valida os parâmetros e constrói a configuração imutável.
         *
         * @return a configuração construída
         * @throws IllegalArgumentException quando algum parâmetro é inválido
         * @since 24/11/2026
         */
        public BcbClientConfig build() {
            BcbClientConfig.Builder.exigirTextoNaoVazio(this.urlBaseSgs, "urlBaseSgs");
            BcbClientConfig.Builder.exigirTextoNaoVazio(this.urlBasePtax, "urlBasePtax");
            BcbClientConfig.Builder.exigirTextoNaoVazio(this.urlBaseExpectativas, "urlBaseExpectativas");
            BcbClientConfig.Builder.exigirTextoNaoVazio(this.userAgent, "userAgent");
            java.util.Objects.requireNonNull(this.timeoutConexao, "timeoutConexao não pode ser nulo.");
            java.util.Objects.requireNonNull(this.timeoutResposta, "timeoutResposta não pode ser nulo.");
            java.util.Objects.requireNonNull(this.backoffInicial, "backoffInicial não pode ser nulo.");
            java.util.Objects.requireNonNull(this.backoffMaximo, "backoffMaximo não pode ser nulo.");
            java.util.Objects.requireNonNull(this.vazaoPorSegundo, "vazaoPorSegundo não pode ser nulo.");
            java.util.Objects.requireNonNull(this.janelaMaximaSgs, "janelaMaximaSgs não pode ser nulo.");
            if (this.maxTentativas == null || this.maxTentativas < 1) {
                throw new IllegalArgumentException("maxTentativas deve ser maior ou igual a 1.");
            }
            if (this.multiplicadorBackoff == null || this.multiplicadorBackoff < 1.0) {
                throw new IllegalArgumentException("multiplicadorBackoff deve ser maior ou igual a 1.0.");
            }
            if (this.tamanhoPaginaFocus == null || this.tamanhoPaginaFocus < 1) {
                throw new IllegalArgumentException("tamanhoPaginaFocus deve ser maior ou igual a 1.");
            }
            return new BcbClientConfig(this);
        }

        private static void exigirTextoNaoVazio(String valor, String nome) {
            if (valor == null || valor.isBlank()) {
                throw new IllegalArgumentException(nome + " não pode ser nulo ou vazio.");
            }
        }
    }
}
