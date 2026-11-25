package solutions.held.bcb.model.sgs;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Valor de uma série temporal do SGS do Banco Central.
 *
 * <p>
 *     Cada registro associa uma data a um valor numérico. Na origem, a data vem no formato
 *     {@code dd/MM/yyyy} e o valor vem como texto com ponto decimal (podendo ser negativo em séries
 *     de variação); aqui são expostos como {@link LocalDate} e {@link BigDecimal}.
 * </p>
 *
 * @param data data de referência do valor
 * @param valor valor numérico da série, possivelmente negativo
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public record ValorSerie(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
        LocalDate data,

        BigDecimal valor) {}
