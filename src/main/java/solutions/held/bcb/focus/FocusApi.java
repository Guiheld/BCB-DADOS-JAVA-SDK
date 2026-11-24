package solutions.held.bcb.focus;

import solutions.held.bcb.http.HttpTransport;
import solutions.held.bcb.model.focus.DataReferenciaFocus;
import solutions.held.bcb.model.focus.ExpectativaInflacao;
import solutions.held.bcb.model.focus.ExpectativaMercado;
import solutions.held.bcb.model.focus.ExpectativaSelic;
import solutions.held.bcb.model.focus.ExpectativaTop5;
import solutions.held.bcb.model.focus.ExpectativaTop5Inflacao;
import solutions.held.bcb.model.focus.ExpectativaTop5Selic;
import solutions.held.bcb.request.ConsultaFocus;

/**
 * Sub-API de acesso ao serviço de Expectativas/Focus do Banco Central.
 *
 * <p>
 *     Cada função devolve uma {@link ConsultaFocus} tipada, vinculada ao EntitySet correspondente,
 *     pronta para receber filtros, ordenação e limites. Os nomes de EntitySet seguem exatamente a
 *     grafia (por vezes irregular) publicada no serviço. É de uso concorrente seguro.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class FocusApi {

    private final HttpTransport transporte;
    private final String urlBase;
    private final Integer tamanhoPagina;

    /**
     * Cria a sub-API do Focus.
     *
     * @param transporte transporte HTTP compartilhado
     * @param urlBase endereço base do serviço Focus
     * @param tamanhoPagina tamanho de página da paginação automática
     * @since 24/11/2026
     */
    public FocusApi(HttpTransport transporte, String urlBase, Integer tamanhoPagina) {
        this.transporte = transporte;
        this.urlBase = urlBase;
        this.tamanhoPagina = tamanhoPagina;
    }

    /**
     * Expectativas de mercado anuais.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    public ConsultaFocus<ExpectativaMercado> anuais() {
        return this.consulta("ExpectativasMercadoAnuais", ExpectativaMercado.class);
    }

    /**
     * Expectativas de mercado mensais.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    public ConsultaFocus<ExpectativaMercado> mensais() {
        return this.consulta("ExpectativaMercadoMensais", ExpectativaMercado.class);
    }

    /**
     * Expectativas de mercado trimestrais.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<ExpectativaMercado> trimestrais() {
        return this.consulta("ExpectativasMercadoTrimestrais", ExpectativaMercado.class);
    }

    /**
     * Expectativas de inflação para os próximos 12 meses.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<ExpectativaInflacao> inflacao12Meses() {
        return this.consulta("ExpectativasMercadoInflacao12Meses", ExpectativaInflacao.class);
    }

    /**
     * Expectativas de inflação para os próximos 24 meses.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<ExpectativaInflacao> inflacao24Meses() {
        return this.consulta("ExpectativasMercadoInflacao24Meses", ExpectativaInflacao.class);
    }

    /**
     * Expectativas de mercado para a taxa Selic.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<ExpectativaSelic> selic() {
        return this.consulta("ExpectativasMercadoSelic", ExpectativaSelic.class);
    }

    /**
     * Expectativas Top 5 anuais.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<ExpectativaTop5> top5Anuais() {
        return this.consulta("ExpectativasMercadoTop5Anuais", ExpectativaTop5.class);
    }

    /**
     * Expectativas Top 5 mensais.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<ExpectativaTop5> top5Mensais() {
        return this.consulta("ExpectativasMercadoTop5Mensais", ExpectativaTop5.class);
    }

    /**
     * Expectativas Top 5 trimestrais (EntitySet de nome singular no serviço).
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<ExpectativaTop5> top5Trimestral() {
        return this.consulta("ExpectativaMercadoTop5Trimestral", ExpectativaTop5.class);
    }

    /**
     * Expectativas Top 5 para a taxa Selic.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    public ConsultaFocus<ExpectativaTop5Selic> top5Selic() {
        return this.consulta("ExpectativasMercadoTop5Selic", ExpectativaTop5Selic.class);
    }

    /**
     * Expectativas Top 5 de inflação para os próximos 12 meses.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<ExpectativaTop5Inflacao> top5Inflacao12Meses() {
        return this.consulta("ExpectativasMercadoTop5Inflacao12Meses", ExpectativaTop5Inflacao.class);
    }

    /**
     * Expectativas Top 5 de inflação para os próximos 24 meses.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<ExpectativaTop5Inflacao> top5Inflacao24Meses() {
        return this.consulta("ExpectativasMercadoTop5Inflacao24Meses", ExpectativaTop5Inflacao.class);
    }

    /**
     * Datas de referência disponíveis por indicador.
     *
     * @return a consulta correspondente
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<DataReferenciaFocus> datasReferencia() {
        return this.consulta("DatasReferencia", DataReferenciaFocus.class);
    }

    private <T> ConsultaFocus<T> consulta(String entitySet, Class<T> tipoItem) {
        return new ConsultaFocus<>(this.transporte, this.urlBase, entitySet, tipoItem, this.tamanhoPagina);
    }
}
