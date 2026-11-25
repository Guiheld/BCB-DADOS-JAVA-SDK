package solutions.held.bcb.model.focus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expectativa de inflação do boletim Focus para os horizontes de 12 e 24 meses.
 *
 * <p>
 *     Os EntitySets de inflação de 12 e 24 meses compartilham o mesmo formato, representado por este
 *     registro. O campo {@code suavizada} indica se a série é suavizada ("S") ou não ("N").
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
 * @param baseCalculo base de cálculo utilizada
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public record ExpectativaInflacao(
        @JsonProperty("Indicador") String indicador,
        @JsonProperty("Data") LocalDate data,
        @JsonProperty("Suavizada") String suavizada,
        @JsonProperty("Media") BigDecimal media,
        @JsonProperty("Mediana") BigDecimal mediana,
        @JsonProperty("DesvioPadrao") BigDecimal desvioPadrao,
        @JsonProperty("Minimo") BigDecimal minimo,
        @JsonProperty("Maximo") BigDecimal maximo,
        Integer numeroRespondentes,
        Integer baseCalculo) {}
