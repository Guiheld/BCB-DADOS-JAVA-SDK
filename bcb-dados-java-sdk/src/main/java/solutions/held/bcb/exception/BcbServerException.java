package solutions.held.bcb.exception;

import java.io.Serial;

/**
 * Exceção para respostas de erro de servidor (HTTP 5xx) das APIs do Banco Central.
 *
 * <p>
 *     Indica uma falha do lado do servidor. É considerada recuperável pela política de novas
 *     tentativas da SDK, sendo lançada apenas após o esgotamento das tentativas configuradas.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public class BcbServerException extends BcbApiException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção de erro de servidor.
     *
     * @param mensagem descrição do erro ocorrido
     * @param codigoStatus código de status HTTP retornado pela API
     * @param corpoResposta corpo bruto da resposta, possivelmente {@code null}
     * @param erro erro já interpretado, possivelmente {@code null}
     * @since 24/11/2026
     */
    public BcbServerException(String mensagem, Integer codigoStatus, String corpoResposta, ErroBcb erro) {
        super(mensagem, codigoStatus, corpoResposta, erro);
    }
}
