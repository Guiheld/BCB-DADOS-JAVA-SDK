package solutions.held.bcb.exception;

import java.io.Serial;
import java.time.Duration;
import java.util.Optional;

/**
 * Exceção lançada quando a API sinaliza excesso de requisições (HTTP 429).
 *
 * <p>
 *     Estende {@link BcbClientException} por tratar-se de um erro de cliente, mas é tratada de forma
 *     específica pela política de novas tentativas: quando o servidor informa o cabeçalho
 *     {@code Retry-After}, o tempo sugerido fica disponível em {@link #tempoEsperaSugerido()}.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public class BcbRateLimitException extends BcbClientException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final transient Duration tempoEsperaSugerido;

    /**
     * Cria a exceção de limite de requisições.
     *
     * @param mensagem descrição do erro ocorrido
     * @param codigoStatus código de status HTTP retornado pela API
     * @param corpoResposta corpo bruto da resposta, possivelmente {@code null}
     * @param tempoEsperaSugerido tempo de espera sugerido pelo servidor, possivelmente {@code null}
     * @since 24/11/2026
     */
    public BcbRateLimitException(
            String mensagem, Integer codigoStatus, String corpoResposta, Duration tempoEsperaSugerido) {
        super(mensagem, codigoStatus, corpoResposta, null);
        this.tempoEsperaSugerido = tempoEsperaSugerido;
    }

    /**
     * Devolve o tempo de espera sugerido pelo servidor, quando informado.
     *
     * @return o tempo de espera sugerido, ou vazio quando não informado
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public Optional<Duration> tempoEsperaSugerido() {
        return Optional.ofNullable(this.tempoEsperaSugerido);
    }
}
