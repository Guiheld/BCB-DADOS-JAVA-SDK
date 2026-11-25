package solutions.held.bcb.model.enums;

import java.util.Optional;

/**
 * Catálogo das séries do SGS mais utilizadas, com código, periodicidade e descrição.
 *
 * <p>
 *     Os códigos foram confirmados junto ao SGS. Observa-se que a série {@link #SELIC_META} (meta
 *     Selic do Copom) é entregue como série diária preenchida — um registro por dia de calendário —
 *     e, por isso, está sujeita ao limite de dez anos por consulta, sendo classificada como
 *     {@link Periodicidade#DIARIA}.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public enum SerieSgs {

    /** Taxa Selic diária acumulada no dia (código 11, % ao dia). */
    SELIC_DIARIA(11, Periodicidade.DIARIA, "Taxa Selic diária (% a.d.)"),

    /** Meta Selic definida pelo Copom (código 432, % ao ano; série diária preenchida). */
    SELIC_META(432, Periodicidade.DIARIA, "Meta Selic definida pelo Copom (% a.a.)"),

    /** Dólar dos Estados Unidos, taxa de venda PTAX (código 1, R$/US$). */
    DOLAR_PTAX_VENDA(1, Periodicidade.DIARIA, "Dólar dos EUA - venda (PTAX) (R$/US$)"),

    /** IPCA, variação percentual mensal (código 433, %). */
    IPCA(433, Periodicidade.MENSAL, "IPCA - variação mensal (%)"),

    /** Taxa CDI diária acumulada no dia (código 12, % ao dia). */
    CDI(12, Periodicidade.DIARIA, "Taxa CDI diária (% a.d.)"),

    /** IGP-M, variação percentual mensal (código 189, %). */
    IGP_M(189, Periodicidade.MENSAL, "IGP-M - variação mensal (%)");

    private final int codigo;
    private final Periodicidade periodicidade;
    private final String descricao;

    SerieSgs(int codigo, Periodicidade periodicidade, String descricao) {
        this.codigo = codigo;
        this.periodicidade = periodicidade;
        this.descricao = descricao;
    }

    /**
     * Devolve o código numérico da série no SGS.
     *
     * @return o código da série
     * @since 24/11/2026
     */
    public int codigo() {
        return this.codigo;
    }

    /**
     * Devolve a periodicidade da série.
     *
     * @return a periodicidade da série
     * @since 24/11/2026
     */
    public Periodicidade periodicidade() {
        return this.periodicidade;
    }

    /**
     * Devolve a descrição legível da série.
     *
     * @return a descrição da série
     * @since 24/11/2026
     */
    public String descricao() {
        return this.descricao;
    }

    /**
     * Procura a série de catálogo correspondente a um código do SGS.
     *
     * @param codigo código numérico da série no SGS
     * @return a série correspondente, ou vazio quando o código não está no catálogo
     * @since 24/11/2026
     */
    public static Optional<SerieSgs> fromCodigo(int codigo) {
        for (SerieSgs serie : values()) {
            if (serie.codigo == codigo) return Optional.of(serie);
        }
        return Optional.empty();
    }
}
