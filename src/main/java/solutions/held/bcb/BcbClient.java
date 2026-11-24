package solutions.held.bcb;

import solutions.held.bcb.cambio.CambioApi;
import solutions.held.bcb.focus.FocusApi;
import solutions.held.bcb.http.HttpTransport;
import solutions.held.bcb.sgs.SgsApi;

/**
 * Ponto de entrada da SDK de dados econômicos do Banco Central.
 *
 * <p>
 *     Reúne as sub-APIs de séries temporais ({@link #sgs()}), câmbio PTAX ({@link #cambio()}) e
 *     Expectativas/Focus ({@link #focus()}). É imutável e seguro para uso concorrente, compartilhando
 *     internamente um único transporte HTTP. Crie com {@link #criar()} para usar a configuração
 *     padrão ou com {@link #comConfig(BcbClientConfig)} para personalizá-la.
 * </p>
 *
 * <pre>{@code
 * BcbClient bcb = BcbClient.criar();
 * bcb.sgs().ultimos(SerieSgs.SELIC_META, 1).forEach(System.out::println);
 * bcb.cambio().cotacaoDolarDia(LocalDate.now()).ifPresent(System.out::println);
 * }</pre>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class BcbClient {

    private final BcbClientConfig config;
    private final SgsApi sgs;
    private final CambioApi cambio;
    private final FocusApi focus;

    private BcbClient(BcbClientConfig config) {
        this.config = config;
        HttpTransport transporte = new HttpTransport(config);
        this.sgs = new SgsApi(transporte, config.urlBaseSgs(), config.janelaMaximaSgs());
        this.cambio = new CambioApi(transporte, config.urlBasePtax());
        this.focus = new FocusApi(transporte, config.urlBaseExpectativas(), config.tamanhoPaginaFocus());
    }

    /**
     * Cria um cliente com a configuração padrão.
     *
     * @return um novo cliente
     * @since 24/11/2026
     */
    public static BcbClient criar() {
        return new BcbClient(BcbClientConfig.padrao());
    }

    /**
     * Cria um cliente com a configuração informada.
     *
     * @param config configuração do cliente
     * @return um novo cliente
     * @since 24/11/2026
     */
    public static BcbClient comConfig(BcbClientConfig config) {
        return new BcbClient(config);
    }

    /**
     * Devolve a sub-API de séries temporais (SGS).
     *
     * @return a sub-API do SGS
     * @since 24/11/2026
     */
    public SgsApi sgs() {
        return this.sgs;
    }

    /**
     * Devolve a sub-API de câmbio (PTAX).
     *
     * @return a sub-API de câmbio
     * @since 24/11/2026
     */
    public CambioApi cambio() {
        return this.cambio;
    }

    /**
     * Devolve a sub-API de Expectativas/Focus.
     *
     * @return a sub-API do Focus
     * @since 24/11/2026
     */
    public FocusApi focus() {
        return this.focus;
    }

    /**
     * Devolve a configuração em uso por este cliente.
     *
     * @return a configuração do cliente
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public BcbClientConfig config() {
        return this.config;
    }
}
