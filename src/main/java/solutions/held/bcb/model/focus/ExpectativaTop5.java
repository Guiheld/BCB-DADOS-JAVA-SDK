package solutions.held.bcb.model.focus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expectativa Top 5 do boletim Focus para os EntitySets anuais, mensais e trimestrais.
 *
 * <p>
 *     As estatísticas Top 5 consideram apenas as cinco instituições de melhor desempenho. O campo
 *     {@code tipoCalculo} distingue o critério (por exemplo, {@code C}). O
 *     {@code numeroRespondentes} só é informado no EntitySet trimestral; nos demais vem nulo.
 * </p>
 *
 * @param indicador indicador econômico
 * @param data data da coleta das expectativas
 * @param dataReferencia período de referência da expectativa, como texto
 * @param tipoCalculo critério de cálculo do Top 5
 * @param media média das expectativas
 * @param mediana mediana das expectativas
 * @param desvioPadrao desvio padrão das expectativas
 * @param minimo valor mínimo informado
 * @param maximo valor máximo informado
 * @param numeroRespondentes número de respondentes, quando informado
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public record ExpectativaTop5(
        @JsonProperty("Indicador") String indicador,
        @JsonProperty("Data") LocalDate data,
        @JsonProperty("DataReferencia") String dataReferencia,
        String tipoCalculo,
        @JsonProperty("Media") BigDecimal media,
        @JsonProperty("Mediana") BigDecimal mediana,
        @JsonProperty("DesvioPadrao") BigDecimal desvioPadrao,
        @JsonProperty("Minimo") BigDecimal minimo,
        @JsonProperty("Maximo") BigDecimal maximo,
        Integer numeroRespondentes) {}
