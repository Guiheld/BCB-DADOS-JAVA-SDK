package solutions.held.bcb.model.cambio;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

/**
 * Desserializador do campo {@code dataHoraCotacao} do serviço PTAX.
 *
 * <p>
 *     O PTAX entrega a data e hora da cotação como texto no formato
 *     {@code yyyy-MM-dd HH:mm:ss.SSSSSS} (espaço entre data e hora, horário de Brasília e sem
 *     indicação de fuso), com fração de segundo de tamanho variável. Este desserializador tolera a
 *     ausência da fração e qualquer quantidade de casas (de zero a nove).
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class DataHoraCotacaoDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATO = new DateTimeFormatterBuilder()
            .appendPattern("yyyy-MM-dd HH:mm:ss")
            .optionalStart()
            .appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true)
            .optionalEnd()
            .toFormatter();

    /**
     * Converte o texto de data e hora da cotação em {@link LocalDateTime}.
     *
     * @param parser leitor do fluxo JSON
     * @param contexto contexto de desserialização
     * @return a data e hora da cotação, ou {@code null} quando o texto é vazio
     * @throws java.io.IOException quando ocorre erro de leitura do JSON
     * @since 24/11/2026
     */
    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext contexto) throws java.io.IOException {
        String texto = parser.getValueAsString();
        if (texto == null || texto.isBlank()) return null;
        return LocalDateTime.parse(texto.strip(), FORMATO);
    }
}
