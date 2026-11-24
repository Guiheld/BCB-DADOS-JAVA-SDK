package solutions.held.bcb.model.focus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expectativa Top 5 para a taxa Selic no boletim Focus.
 *
 * <p>
 *     Este EntitySet tem um formato peculiar: a maioria dos campos vem em caixa baixa e inclui o
 *     {@code coeficienteVariacao}, exclusivo deste tipo, além de não trazer número de respondentes
 *     nem base de cálculo. Apenas o campo {@code Data} vem capitalizado.
 * </p>
 *
 * @param indicador indicador econômico ({@code Selic})
 * @param data data da coleta das expectativas
 * @param reuniao reunião do Copom de referência
 * @param tipoCalculo critério de cálculo do Top 5
 * @param media média das expectativas
 * @param mediana mediana das expectativas
 * @param desvioPadrao desvio padrão das expectativas
 * @param coeficienteVariacao coeficiente de variação das expectativas
 * @param minimo valor mínimo informado
 * @param maximo valor máximo informado
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public record ExpectativaTop5Selic(
        String indicador,
        @JsonProperty("Data") LocalDate data,
        String reuniao,
        String tipoCalculo,
        BigDecimal media,
        BigDecimal mediana,
        BigDecimal desvioPadrao,
        BigDecimal coeficienteVariacao,
        BigDecimal minimo,
        BigDecimal maximo) {}
