package solutions.held.bcb.model.enums;

import java.util.Optional;

/**
 * Tipo de boletim de uma cotação de câmbio PTAX.
 *
 * <p>
 *     O serviço PTAX publica vários boletins ao longo do dia. O rótulo exato enviado pela API é
 *     preservado em {@link #rotulo()} e pode ser recuperado a partir do texto por
 *     {@link #fromRotulo(String)}.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public enum TipoBoletim {

    /** Boletim de abertura. */
    ABERTURA("Abertura"),

    /** Boletim intermediário. */
    INTERMEDIARIO("Intermediário"),

    /** Boletim de fechamento (PTAX de fechamento). */
    FECHAMENTO_PTAX("Fechamento PTAX");

    private final String rotulo;

    TipoBoletim(String rotulo) {
        this.rotulo = rotulo;
    }

    /**
     * Devolve o rótulo exato usado pela API para este tipo de boletim.
     *
     * @return o rótulo do boletim
     * @since 24/11/2026
     */
    public String rotulo() {
        return this.rotulo;
    }

    /**
     * Procura o tipo de boletim correspondente a um rótulo da API, ignorando diferenças de caixa.
     *
     * @param rotulo rótulo recebido da API
     * @return o tipo correspondente, ou vazio quando não reconhecido
     * @since 24/11/2026
     */
    public static Optional<TipoBoletim> fromRotulo(String rotulo) {
        if (rotulo == null) return Optional.empty();
        for (TipoBoletim tipo : values()) {
            if (tipo.rotulo.equalsIgnoreCase(rotulo.strip())) return Optional.of(tipo);
        }
        return Optional.empty();
    }
}
