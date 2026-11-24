package solutions.held.bcb.exception;

import java.io.Serial;

/**
 * Exceção para respostas de erro de cliente (HTTP 4xx) das APIs do Banco Central.
 *
 * <p>
 *     Indica que a requisição foi rejeitada por conta dos parâmetros enviados (por exemplo, recurso
 *     inexistente ou parâmetro inválido). Não é recuperável por nova tentativa automática. As
 *     condições específicas de limite de requisições e de janela de consulta são sinalizadas pelas
 *     subclasses {@link BcbRateLimitException} e {@link BcbLimiteJanelaException}.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public class BcbClientException extends BcbApiException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção de erro de cliente.
     *
     * @param mensagem descrição do erro ocorrido
     * @param codigoStatus código de status HTTP retornado pela API
     * @param corpoResposta corpo bruto da resposta, possivelmente {@code null}
     * @param erro erro já interpretado, possivelmente {@code null}
     * @since 24/11/2026
     */
    public BcbClientException(String mensagem, Integer codigoStatus, String corpoResposta, ErroBcb erro) {
        super(mensagem, codigoStatus, corpoResposta, erro);
    }
}
