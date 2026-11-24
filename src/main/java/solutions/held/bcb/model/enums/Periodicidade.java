package solutions.held.bcb.model.enums;

/**
 * Periodicidade de uma série temporal do SGS.
 *
 * <p>
 *     Determina se a consulta precisa ser fatiada em janelas de até dez anos: apenas as séries de
 *     periodicidade {@link #DIARIA} estão sujeitas a esse limite imposto pela API. As demais podem
 *     ser consultadas em qualquer intervalo.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public enum Periodicidade {

    /** Série diária (um registro por dia; sujeita ao limite de dez anos por consulta). */
    DIARIA,

    /** Série mensal (isenta do limite de janela). */
    MENSAL,

    /** Série trimestral (isenta do limite de janela). */
    TRIMESTRAL,

    /** Série anual (isenta do limite de janela). */
    ANUAL;

    /**
     * Indica se a periodicidade exige o fatiamento da consulta em janelas de até dez anos.
     *
     * @return {@code true} apenas para séries diárias
     * @since 24/11/2026
     */
    public boolean exigeJanelamento() {
        return this == DIARIA;
    }
}
