package solutions.held.bcb.request;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import solutions.held.bcb.http.ConstrutorOData;
import solutions.held.bcb.http.HttpTransport;
import solutions.held.bcb.stream.PaginadorOData;

/**
 * Consulta fluente a um EntitySet do serviço de Expectativas/Focus do Banco Central.
 *
 * <p>
 *     Acumula filtros, ordenação, projeção e limite, compondo uma consulta OData sobre o EntitySet
 *     associado. O resultado pode ser obtido de uma vez com {@link #buscar()} (respeitando o
 *     {@link #top(int)} informado) ou percorrido integralmente com {@link #stream()}, que pagina
 *     automaticamente via {@code $skip}. Os filtros de data usam comparação lexicográfica sobre a
 *     data em formato ISO, adequada às datas do Focus.
 * </p>
 *
 * @param <T> tipo dos registros devolvidos pela consulta
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class ConsultaFocus<T> {

    private final HttpTransport transporte;
    private final String urlBase;
    private final String entitySet;
    private final Class<T> tipoItem;
    private final Integer tamanhoPagina;
    private final List<String> filtros = new ArrayList<>();
    private String ordenarPor;
    private Integer top;
    private String[] selecionar;

    /**
     * Cria a consulta vinculada a um EntitySet.
     *
     * @param transporte transporte HTTP compartilhado
     * @param urlBase endereço base do serviço Focus
     * @param entitySet nome exato do EntitySet
     * @param tipoItem classe dos registros devolvidos
     * @param tamanhoPagina tamanho de página usado na paginação automática
     * @since 24/11/2026
     */
    public ConsultaFocus(
            HttpTransport transporte, String urlBase, String entitySet, Class<T> tipoItem, Integer tamanhoPagina) {
        this.transporte = transporte;
        this.urlBase = urlBase;
        this.entitySet = entitySet;
        this.tipoItem = tipoItem;
        this.tamanhoPagina = tamanhoPagina;
    }

    /**
     * Filtra pelo indicador econômico.
     *
     * @param indicador nome do indicador (por exemplo, {@code IPCA})
     * @return esta consulta
     * @since 24/11/2026
     */
    public ConsultaFocus<T> indicador(String indicador) {
        this.filtros.add("Indicador eq '" + indicador + "'");
        return this;
    }

    /**
     * Filtra pela data de referência exata (texto), como {@code 2026} ou {@code 11/2028}.
     *
     * @param dataReferencia data de referência, como texto
     * @return esta consulta
     * @since 24/11/2026
     */
    public ConsultaFocus<T> dataReferencia(String dataReferencia) {
        this.filtros.add("DataReferencia eq '" + dataReferencia + "'");
        return this;
    }

    /**
     * Filtra pelas coletas a partir da data informada (inclusive).
     *
     * @param data data mínima de coleta
     * @return esta consulta
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<T> desde(LocalDate data) {
        this.filtros.add("Data ge '" + data + "'");
        return this;
    }

    /**
     * Filtra pelas coletas até a data informada (inclusive).
     *
     * @param data data máxima de coleta
     * @return esta consulta
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<T> ate(LocalDate data) {
        this.filtros.add("Data le '" + data + "'");
        return this;
    }

    /**
     * Adiciona uma expressão de filtro OData bruta, combinada com as demais por {@code and}.
     *
     * @param expressao expressão de filtro OData
     * @return esta consulta
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public ConsultaFocus<T> filtro(String expressao) {
        this.filtros.add(expressao);
        return this;
    }

    /**
     * Define a ordenação dos resultados por um campo.
     *
     * @param campo nome do campo de ordenação
     * @param descendente {@code true} para ordem decrescente, {@code false} para crescente
     * @return esta consulta
     * @since 24/11/2026
     */
    public ConsultaFocus<T> ordenarPor(String campo, boolean descendente) {
        this.ordenarPor = campo + (descendente ? " desc" : " asc");
        return this;
    }

    /**
     * Limita o número de registros devolvidos por {@link #buscar()}.
     *
     * @param top número máximo de registros
     * @return esta consulta
     * @since 24/11/2026
     */
    public ConsultaFocus<T> top(int top) {
        this.top = top;
        return this;
    }

    /**
     * Projeta apenas os campos informados.
     *
     * @param campos campos a projetar
     * @return esta consulta
     * @since 24/11/2026
     */
    public ConsultaFocus<T> selecionar(String... campos) {
        this.selecionar = campos;
        return this;
    }

    /**
     * Executa a consulta e devolve os registros de uma só vez.
     *
     * @return a lista de registros
     * @since 24/11/2026
     */
    public List<T> buscar() {
        return this.transporte.obterListaOData(this.montar(this.top, null), this.tipoItem);
    }

    /**
     * Executa a consulta como fluxo preguiçoso, paginando automaticamente todos os registros.
     *
     * @return um fluxo com todos os registros que satisfazem a consulta
     * @since 24/11/2026
     */
    public Stream<T> stream() {
        return PaginadorOData.paginar(
                this.tamanhoPagina,
                (skip, tamanho) -> this.transporte.obterListaOData(this.montar(tamanho, skip), this.tipoItem));
    }

    private String montar(Integer top, Integer skip) {
        ConstrutorOData construtor = ConstrutorOData.entidade(this.urlBase, this.entitySet);
        if (!this.filtros.isEmpty()) construtor.filtro(String.join(" and ", this.filtros));
        if (this.ordenarPor != null) construtor.ordenarPor(this.ordenarPor);
        if (this.selecionar != null) construtor.selecionar(this.selecionar);
        if (top != null) construtor.top(top);
        if (skip != null) construtor.skip(skip);
        return construtor.construir();
    }
}
