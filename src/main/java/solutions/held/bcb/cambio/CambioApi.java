package solutions.held.bcb.cambio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import solutions.held.bcb.http.ConstrutorOData;
import solutions.held.bcb.http.HttpTransport;
import solutions.held.bcb.model.cambio.CotacaoDolar;
import solutions.held.bcb.model.cambio.CotacaoMoeda;
import solutions.held.bcb.model.cambio.Moeda;

/**
 * Sub-API de acesso ao serviço de câmbio PTAX do Banco Central.
 *
 * <p>
 *     Expõe as cotações do dólar e de outras moedas, por dia ou por período, além da lista de moedas
 *     disponíveis. Datas são informadas como {@link LocalDate} e convertidas internamente para o
 *     formato exigido pela API. Um dia sem cotação (fim de semana, feriado ou data futura) resulta em
 *     resultado vazio, nunca em erro. É de uso concorrente seguro.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class CambioApi {

    private final HttpTransport transporte;
    private final String urlBase;

    /**
     * Cria a sub-API de câmbio.
     *
     * @param transporte transporte HTTP compartilhado
     * @param urlBase endereço base do serviço PTAX
     * @since 24/11/2026
     */
    public CambioApi(HttpTransport transporte, String urlBase) {
        this.transporte = transporte;
        this.urlBase = urlBase;
    }

    /**
     * Obtém a cotação do dólar em um dia.
     *
     * @param data data da cotação
     * @return a cotação do dia, ou vazio quando não há cotação na data
     * @since 24/11/2026
     */
    public Optional<CotacaoDolar> cotacaoDolarDia(LocalDate data) {
        List<CotacaoDolar> lista = new ArrayList<>(this.transporte.obterListaOData(
                ConstrutorOData.funcao(this.urlBase, "CotacaoDolarDia")
                        .parametroData("dataCotacao", data)
                        .construir(),
                CotacaoDolar.class));
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    /**
     * Obtém as cotações do dólar em um período.
     *
     * @param dataInicial data inicial, inclusive
     * @param dataFinalCotacao data final, inclusive
     * @return a lista de cotações no período
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public List<CotacaoDolar> cotacaoDolarPeriodo(LocalDate dataInicial, LocalDate dataFinalCotacao) {
        return this.transporte.obterListaOData(
                ConstrutorOData.funcao(this.urlBase, "CotacaoDolarPeriodo")
                        .parametroData("dataInicial", dataInicial)
                        .parametroData("dataFinalCotacao", dataFinalCotacao)
                        .construir(),
                CotacaoDolar.class);
    }

    /**
     * Obtém todos os boletins de cotação de uma moeda em um dia.
     *
     * @param moeda símbolo de três letras da moeda (por exemplo, {@code EUR})
     * @param data data da cotação
     * @return a lista de boletins do dia
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public List<CotacaoMoeda> cotacaoMoedaDia(String moeda, LocalDate data) {
        return this.transporte.obterListaOData(
                ConstrutorOData.funcao(this.urlBase, "CotacaoMoedaDia")
                        .parametroTexto("moeda", moeda)
                        .parametroData("dataCotacao", data)
                        .construir(),
                CotacaoMoeda.class);
    }

    /**
     * Obtém apenas o boletim de fechamento PTAX de uma moeda em um dia.
     *
     * @param moeda símbolo de três letras da moeda (por exemplo, {@code EUR})
     * @param data data da cotação
     * @return o boletim de fechamento, ou vazio quando não há cotação na data
     * @since 24/11/2026
     */
    @SuppressWarnings("UnusedReturnValue")
    public Optional<CotacaoMoeda> cotacaoMoedaDiaFechamento(String moeda, LocalDate data) {
        List<CotacaoMoeda> lista = new ArrayList<>(this.transporte.obterListaOData(
                ConstrutorOData.funcao(this.urlBase, "CotacaoMoedaDia")
                        .parametroTexto("moeda", moeda)
                        .parametroData("dataCotacao", data)
                        .filtro("tipoBoletim eq 'Fechamento PTAX'")
                        .construir(),
                CotacaoMoeda.class));
        return lista.isEmpty() ? Optional.empty() : Optional.of(lista.get(0));
    }

    /**
     * Obtém as cotações de uma moeda em um período.
     *
     * @param moeda símbolo de três letras da moeda (por exemplo, {@code EUR})
     * @param dataInicial data inicial, inclusive
     * @param dataFinalCotacao data final, inclusive
     * @return a lista de cotações no período
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public List<CotacaoMoeda> cotacaoMoedaPeriodo(String moeda, LocalDate dataInicial, LocalDate dataFinalCotacao) {
        return this.transporte.obterListaOData(
                ConstrutorOData.funcao(this.urlBase, "CotacaoMoedaPeriodo")
                        .parametroTexto("moeda", moeda)
                        .parametroData("dataInicial", dataInicial)
                        .parametroData("dataFinalCotacao", dataFinalCotacao)
                        .construir(),
                CotacaoMoeda.class);
    }

    /**
     * Obtém a lista de moedas disponíveis no serviço PTAX.
     *
     * @return a lista de moedas
     * @since 24/11/2026
     */
    @SuppressWarnings("UnusedReturnValue")
    public List<Moeda> moedas() {
        return this.transporte.obterListaOData(
                ConstrutorOData.entidade(this.urlBase, "Moedas").construir(), Moeda.class);
    }
}
