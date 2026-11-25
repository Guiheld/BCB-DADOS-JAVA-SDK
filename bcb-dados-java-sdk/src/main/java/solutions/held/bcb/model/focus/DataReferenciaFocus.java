package solutions.held.bcb.model.focus;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Datas de referência disponíveis por indicador no boletim Focus.
 *
 * <p>
 *     Corresponde ao EntitySet {@code DatasReferencia}, que relaciona, por indicador e período, as
 *     datas de referência válidas. Os valores são mantidos como texto, pois seu formato varia.
 * </p>
 *
 * @param indicador indicador econômico
 * @param periodo período associado
 * @param dataReferencia1 primeira data de referência
 * @param dataReferencia2 segunda data de referência
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public record DataReferenciaFocus(
        @JsonProperty("Indicador") String indicador,
        String periodo,
        @JsonProperty("DataReferencia1") String dataReferencia1,
        @JsonProperty("DataReferencia2") String dataReferencia2) {}
