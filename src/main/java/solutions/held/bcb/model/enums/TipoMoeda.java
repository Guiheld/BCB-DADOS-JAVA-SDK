package solutions.held.bcb.model.enums;

import java.util.Optional;

/**
 * Tipo de paridade de uma moeda no serviço PTAX.
 *
 * <p>
 *     Indica como a paridade da moeda em relação ao dólar é expressa. O código de uma letra enviado
 *     pela API pode ser convertido por {@link #fromCodigo(String)}.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public enum TipoMoeda {

    /** Paridade expressa como unidade de moeda por dólar (código "A"). */
    A("A", "Unidade de moeda por dólar"),

    /** Paridade expressa como dólar por unidade de moeda (código "B"). */
    B("B", "Dólar por unidade de moeda");

    private final String codigo;
    private final String descricao;

    @SuppressWarnings("unused")
    TipoMoeda(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    /**
     * Devolve o código de uma letra usado pela API.
     *
     * @return o código do tipo de moeda
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public String codigo() {
        return this.codigo;
    }

    /**
     * Devolve a descrição legível do tipo de paridade.
     *
     * @return a descrição do tipo de moeda
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public String descricao() {
        return this.descricao;
    }

    /**
     * Procura o tipo de moeda correspondente a um código da API, ignorando diferenças de caixa.
     *
     * @param codigo código de uma letra recebido da API
     * @return o tipo correspondente, ou vazio quando não reconhecido
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public static Optional<TipoMoeda> fromCodigo(String codigo) {
        if (codigo == null) return Optional.empty();

        for (TipoMoeda tipo : values()) {
            if (tipo.codigo.equalsIgnoreCase(codigo.strip())) return Optional.of(tipo);
        }
        return Optional.empty();
    }
}
