package solutions.held.bcb.http;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Construtor reutilizável de URLs OData (v4) para os serviços Olinda do Banco Central (PTAX e Focus).
 *
 * <p>
 *     Monta tanto chamadas de função (no formato {@code Funcao(p=@p)?@p='valor'}) quanto consultas a
 *     EntitySets, aplicando as opções de sistema {@code $filter}, {@code $orderby}, {@code $top},
 *     {@code $skip} e {@code $select}, sempre com {@code $format=json}. Os valores de parâmetro de
 *     função vão entre aspas simples e as datas são formatadas no padrão {@code MM-dd-yyyy} exigido
 *     pelo PTAX. <b>É de uso interno da SDK.</b>
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class ConstrutorOData {

    private static final DateTimeFormatter FORMATO_DATA_PTAX = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    private final String base;
    private final String recurso;
    private final Boolean funcao;
    private final Map<String, String> parametrosFuncao = new LinkedHashMap<>();
    private String filtro;
    private String ordenarPor;
    private String selecionar;
    private Integer top;
    private Integer skip;

    private ConstrutorOData(String base, String recurso, Boolean funcao) {
        this.base = base;
        this.recurso = recurso;
        this.funcao = funcao;
    }

    /**
     * Inicia a construção de uma chamada de função OData.
     *
     * @param base endereço base do serviço
     * @param nomeFuncao nome da função OData
     * @return um novo construtor
     * @since 24/11/2026
     */
    public static ConstrutorOData funcao(String base, String nomeFuncao) {
        return new ConstrutorOData(base, nomeFuncao, true);
    }

    /**
     * Inicia a construção de uma consulta a um EntitySet OData.
     *
     * @param base endereço base do serviço
     * @param nomeEntidade nome do EntitySet
     * @return um novo construtor
     * @since 24/11/2026
     */
    public static ConstrutorOData entidade(String base, String nomeEntidade) {
        return new ConstrutorOData(base, nomeEntidade, false);
    }

    /**
     * Adiciona um parâmetro de função do tipo data, formatado em {@code MM-dd-yyyy}.
     *
     * @param nome nome do parâmetro
     * @param data data do parâmetro
     * @return este construtor
     * @since 24/11/2026
     */
    public ConstrutorOData parametroData(String nome, LocalDate data) {
        this.parametrosFuncao.put(nome, "'" + data.format(FORMATO_DATA_PTAX) + "'");
        return this;
    }

    /**
     * Adiciona um parâmetro de função do tipo texto, envolto em aspas simples.
     *
     * @param nome nome do parâmetro
     * @param valor valor textual do parâmetro
     * @return este construtor
     * @since 24/11/2026
     */
    public ConstrutorOData parametroTexto(String nome, String valor) {
        this.parametrosFuncao.put(nome, "'" + valor + "'");
        return this;
    }

    /**
     * Define a expressão {@code $filter}.
     *
     * @param filtro expressão de filtro OData
     * @return este construtor
     * @since 24/11/2026
     */
    public ConstrutorOData filtro(String filtro) {
        this.filtro = filtro;
        return this;
    }

    /**
     * Define a expressão {@code $orderby}.
     *
     * @param ordenarPor expressão de ordenação OData
     * @return este construtor
     * @since 24/11/2026
     */
    public ConstrutorOData ordenarPor(String ordenarPor) {
        this.ordenarPor = ordenarPor;
        return this;
    }

    /**
     * Define os campos de {@code $select}.
     *
     * @param campos campos a projetar
     * @return este construtor
     * @since 24/11/2026
     */
    public ConstrutorOData selecionar(String... campos) {
        this.selecionar = String.join(",", campos);
        return this;
    }

    /**
     * Define a opção {@code $top}.
     *
     * @param top número máximo de registros
     * @return este construtor
     * @since 24/11/2026
     */
    public ConstrutorOData top(Integer top) {
        this.top = top;
        return this;
    }

    /**
     * Define a opção {@code $skip}.
     *
     * @param skip número de registros a pular
     * @return este construtor
     * @since 24/11/2026
     */
    public ConstrutorOData skip(Integer skip) {
        this.skip = skip;
        return this;
    }

    /**
     * Monta a URL OData completa.
     *
     * @return a URL montada
     * @since 24/11/2026
     */
    public String construir() {
        StringBuilder url = new StringBuilder(this.base);
        url.append('/').append(this.recurso);
        List<String> opcoes = new ArrayList<>();
        if (this.funcao.equals(Boolean.TRUE)) {
            StringBuilder assinatura = new StringBuilder();
            for (Map.Entry<String, String> parametro : this.parametrosFuncao.entrySet()) {
                if (!assinatura.isEmpty()) assinatura.append(',');
                assinatura.append(parametro.getKey()).append("=@").append(parametro.getKey());
                opcoes.add("@" + parametro.getKey() + "=" + parametro.getValue());
            }
            url.append('(').append(assinatura).append(')');
        }
        if (this.filtro != null) opcoes.add("$filter=" + codificar(this.filtro));
        if (this.ordenarPor != null) opcoes.add("$orderby=" + codificar(this.ordenarPor));
        if (this.selecionar != null) opcoes.add("$select=" + codificar(this.selecionar));
        if (this.top != null) opcoes.add("$top=" + this.top);
        if (this.skip != null) opcoes.add("$skip=" + this.skip);
        opcoes.add("$format=json");
        url.append('?').append(String.join("&", opcoes));
        return url.toString();
    }

    private static String codificar(String valor) {
        return valor.replace(" ", "%20");
    }
}
