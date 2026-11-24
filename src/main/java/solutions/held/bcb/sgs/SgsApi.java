package solutions.held.bcb.sgs;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.stream.Stream;
import solutions.held.bcb.http.HttpTransport;
import solutions.held.bcb.http.QueryParams;
import solutions.held.bcb.model.enums.Periodicidade;
import solutions.held.bcb.model.enums.SerieSgs;
import solutions.held.bcb.model.sgs.ValorSerie;
import solutions.held.bcb.stream.JaneladorSgs;

/**
 * Sub-API de acesso ao SGS (Sistema Gerenciador de Séries Temporais) do Banco Central.
 *
 * <p>
 *     Permite consultar valores de séries por intervalo de datas ou obter os últimos registros. Para
 *     séries de periodicidade diária, as consultas por intervalo são automaticamente fatiadas em
 *     janelas de até dez anos (limite imposto pela API) e os resultados concatenados, expostos como
 *     {@link Stream} preguiçoso. É de uso concorrente seguro.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class SgsApi {

    private final HttpTransport transporte;
    private final String urlBase;
    private final Period janelaMaxima;

    /**
     * Cria a sub-API do SGS.
     *
     * @param transporte transporte HTTP compartilhado
     * @param urlBase endereço base do serviço SGS
     * @param janelaMaxima janela máxima usada no fatiamento das séries diárias
     * @since 24/11/2026
     */
    public SgsApi(HttpTransport transporte, String urlBase, Period janelaMaxima) {
        this.transporte = transporte;
        this.urlBase = urlBase;
        this.janelaMaxima = janelaMaxima;
    }

    /**
     * Consulta os valores de uma série do catálogo em um intervalo de datas.
     *
     * @param serie série do catálogo
     * @param inicio data inicial, inclusive
     * @param fim data final, inclusive
     * @return a lista de valores no intervalo
     * @since 24/11/2026
     */
    public List<ValorSerie> consultar(SerieSgs serie, LocalDate inicio, LocalDate fim) {
        java.util.Objects.requireNonNull(serie, "serie não pode ser nula.");
        return this.consultar(serie.codigo(), serie.periodicidade(), inicio, fim);
    }

    /**
     * Consulta os valores de uma série arbitrária em um intervalo de datas.
     *
     * @param codigo código da série no SGS
     * @param periodicidade periodicidade da série; {@code null} assume comportamento diário (seguro)
     * @param inicio data inicial, inclusive
     * @param fim data final, inclusive
     * @return a lista de valores no intervalo
     * @since 24/11/2026
     */
    public List<ValorSerie> consultar(int codigo, Periodicidade periodicidade, LocalDate inicio, LocalDate fim) {
        return this.stream(codigo, periodicidade, inicio, fim).toList();
    }

    /**
     * Consulta, como fluxo preguiçoso, os valores de uma série do catálogo em um intervalo.
     *
     * @param serie série do catálogo
     * @param inicio data inicial, inclusive
     * @param fim data final, inclusive
     * @return um fluxo com os valores no intervalo
     * @since 24/11/2026
     */
    public Stream<ValorSerie> stream(SerieSgs serie, LocalDate inicio, LocalDate fim) {
        java.util.Objects.requireNonNull(serie, "serie não pode ser nula.");
        return this.stream(serie.codigo(), serie.periodicidade(), inicio, fim);
    }

    /**
     * Consulta, como fluxo preguiçoso, os valores de uma série arbitrária em um intervalo.
     *
     * @param codigo código da série no SGS
     * @param periodicidade periodicidade da série; {@code null} assume comportamento diário (seguro)
     * @param inicio data inicial, inclusive
     * @param fim data final, inclusive
     * @return um fluxo com os valores no intervalo
     * @since 24/11/2026
     */
    public Stream<ValorSerie> stream(Integer codigo, Periodicidade periodicidade, LocalDate inicio, LocalDate fim) {
        java.util.Objects.requireNonNull(inicio, "inicio não pode ser nulo.");
        java.util.Objects.requireNonNull(fim, "fim não pode ser nulo.");
        if (periodicidade == null || periodicidade.exigeJanelamento()) {
            return JaneladorSgs.janelar(
                    inicio, fim, this.janelaMaxima, (ini, fimJanela) -> this.buscarIntervalo(codigo, ini, fimJanela));
        }
        return this.buscarIntervalo(codigo, inicio, fim).stream();
    }

    /**
     * Obtém os últimos valores de uma série do catálogo.
     *
     * @param serie série do catálogo
     * @param quantidade número de registros mais recentes a obter
     * @return a lista dos últimos valores, em ordem cronológica crescente
     * @since 24/11/2026
     */
    public List<ValorSerie> ultimos(SerieSgs serie, Integer quantidade) {
        java.util.Objects.requireNonNull(serie, "serie não pode ser nula.");
        return this.ultimos(serie.codigo(), quantidade);
    }

    /**
     * Obtém os últimos valores de uma série arbitrária.
     *
     * @param codigo código da série no SGS
     * @param quantidade número de registros mais recentes a obter
     * @return a lista dos últimos valores, em ordem cronológica crescente
     * @since 24/11/2026
     */
    public List<ValorSerie> ultimos(Integer codigo, Integer quantidade) {
        return this.transporte.obterListaSgs(
                this.urlBase + "/bcdata.sgs." + codigo + "/dados/ultimos/" + quantidade + "?formato=json",
                ValorSerie.class);
    }

    private List<ValorSerie> buscarIntervalo(Integer codigo, LocalDate inicio, LocalDate fim) {
        QueryParams params = new QueryParams()
                .adicionar("formato", "json")
                .adicionarData("dataInicial", inicio)
                .adicionarData("dataFinal", fim);
        return this.transporte.obterListaSgs(
                this.urlBase + "/bcdata.sgs." + codigo + "/dados?" + params.paraQueryString(), ValorSerie.class);
    }
}
