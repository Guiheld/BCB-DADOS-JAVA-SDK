package solutions.held.bcb.model.focus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expectativa de mercado para a taxa Selic no boletim Focus.
 *
 * <p>
 *     Diferentemente dos demais EntitySets periódicos, a referência é a reunião do Copom (campo
 *     {@code reuniao}, por exemplo {@code R8/2026}) e não há {@code dataReferencia}.
 * </p>
 *
 * @param indicador indicador econômico ({@code Selic})
 * @param data data da coleta das expectativas
 * @param reuniao reunião do Copom de referência (por exemplo, {@code R8/2026})
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
public record ExpectativaSelic(
        @JsonProperty("Indicador") String indicador,
        @JsonProperty("Data") LocalDate data,
        @JsonProperty("Reuniao") String reuniao,
        @JsonProperty("Media") BigDecimal media,
        @JsonProperty("Mediana") BigDecimal mediana,
        @JsonProperty("DesvioPadrao") BigDecimal desvioPadrao,
        @JsonProperty("Minimo") BigDecimal minimo,
        @JsonProperty("Maximo") BigDecimal maximo,
        Integer numeroRespondentes,
        Integer baseCalculo) {}
