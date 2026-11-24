package solutions.held.bcb.model.focus;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Expectativa de mercado do boletim Focus para os EntitySets anuais, mensais e trimestrais.
 *
 * <p>
 *     A {@code dataReferencia} é um texto cujo formato varia conforme o EntitySet (ano, mês/ano ou
 *     trimestre/ano), por isso é mantida como {@link String}. O campo {@code indicadorDetalhe} só é
 *     preenchido no EntitySet anual.
 * </p>
 *
 * @param indicador indicador econômico (por exemplo, {@code IPCA})
 * @param indicadorDetalhe detalhamento do indicador, quando houver
 * @param data data da coleta das expectativas
 * @param dataReferencia período de referência da expectativa, como texto
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
public record ExpectativaMercado(
        @JsonProperty("Indicador") String indicador,
        @JsonProperty("IndicadorDetalhe") String indicadorDetalhe,
        @JsonProperty("Data") LocalDate data,
        @JsonProperty("DataReferencia") String dataReferencia,
        @JsonProperty("Media") BigDecimal media,
        @JsonProperty("Mediana") BigDecimal mediana,
        @JsonProperty("DesvioPadrao") BigDecimal desvioPadrao,
        @JsonProperty("Minimo") BigDecimal minimo,
        @JsonProperty("Maximo") BigDecimal maximo,
        Integer numeroRespondentes,
        Integer baseCalculo) {}
