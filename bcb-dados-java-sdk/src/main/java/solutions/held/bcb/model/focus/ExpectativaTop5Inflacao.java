package solutions.held.bcb.model.focus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expectativa Top 5 de inflação do boletim Focus para os horizontes de 12 e 24 meses.
 *
 * <p>
 *     Os EntitySets Top 5 de inflação de 12 e 24 meses compartilham este formato. Diferem dos
 *     EntitySets de inflação comuns por terem {@code tipoCalculo} (por exemplo, {@code CURTO_PRAZO})
 *     em vez de base de cálculo.
 * </p>
 *
 * @param indicador indicador econômico
 * @param data data da coleta das expectativas
 * @param suavizada indicação de série suavizada ("S" ou "N")
 * @param media média das expectativas
 * @param mediana mediana das expectativas
 * @param desvioPadrao desvio padrão das expectativas
 * @param minimo valor mínimo informado
 * @param maximo valor máximo informado
 * @param numeroRespondentes número de instituições respondentes
 * @param tipoCalculo critério de cálculo do Top 5
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public record ExpectativaTop5Inflacao(
        @JsonProperty("Indicador") String indicador,
        @JsonProperty("Data") LocalDate data,
        @JsonProperty("Suavizada") String suavizada,
        @JsonProperty("Media") BigDecimal media,
        @JsonProperty("Mediana") BigDecimal mediana,
        @JsonProperty("DesvioPadrao") BigDecimal desvioPadrao,
        @JsonProperty("Minimo") BigDecimal minimo,
        @JsonProperty("Maximo") BigDecimal maximo,
        Integer numeroRespondentes,
        String tipoCalculo) {}
