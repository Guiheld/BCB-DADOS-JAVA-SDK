package solutions.held.bcb.http;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Construtor de query string simples para o serviço SGS do Banco Central.
 *
 * <p>
 *     Preserva a ordem de inserção, ignora valores nulos e formata datas no padrão {@code dd/MM/yyyy}
 *     exigido pelo SGS. É de uso interno da SDK.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class QueryParams {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final Map<String, String> parametros = new LinkedHashMap<>();

    /**
     * Adiciona um parâmetro textual, ignorando valores nulos.
     *
     * @param nome nome do parâmetro
     * @param valor valor do parâmetro; quando {@code null}, nada é adicionado
     * @return este construtor
     * @since 24/11/2026
     */
    public QueryParams adicionar(String nome, Object valor) {
        if (valor != null) this.parametros.put(nome, String.valueOf(valor));
        return this;
    }

    /**
     * Adiciona um parâmetro de data no formato {@code dd/MM/yyyy}, ignorando valores nulos.
     *
     * @param nome nome do parâmetro
     * @param data data a formatar; quando {@code null}, nada é adicionado
     * @return este construtor
     * @since 24/11/2026
     */
    public QueryParams adicionarData(String nome, LocalDate data) {
        if (data != null) this.parametros.put(nome, data.format(FORMATO_DATA));
        return this;
    }

    /**
     * Indica se nenhum parâmetro foi adicionado.
     *
     * @return {@code true} quando não há parâmetros
     * @since 24/11/2026
     */
    public boolean vazio() {
        return this.parametros.isEmpty();
    }

    /**
     * Monta a query string correspondente, sem o caractere {@code ?} inicial.
     *
     * @return a query string montada
     * @since 24/11/2026
     */
    public String paraQueryString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, String> entrada : this.parametros.entrySet()) {
            if (!stringBuilder.isEmpty()) stringBuilder.append('&');
            stringBuilder.append(entrada.getKey()).append('=').append(entrada.getValue());
        }
        return stringBuilder.toString();
    }
}
