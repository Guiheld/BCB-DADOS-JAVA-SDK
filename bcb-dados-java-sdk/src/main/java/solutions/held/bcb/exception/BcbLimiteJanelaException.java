package solutions.held.bcb.exception;

import java.io.Serial;

/**
 * Exceção lançada quando uma consulta a série de periodicidade diária do SGS excede a janela máxima
 * de dez anos aceita pela API (HTTP 406).
 *
 * <p>
 *     Em condições normais essa situação é evitada pelo janelamento automático da SDK, que fatia o
 *     período solicitado em janelas de até dez anos. A exceção só ocorre quando uma consulta crua é
 *     feita fora desse mecanismo. A mensagem original da API fica acessível via {@link #erro()}.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public class BcbLimiteJanelaException extends BcbClientException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção de estouro da janela de consulta.
     *
     * @param mensagem descrição do erro ocorrido
     * @param codigoStatus código de status HTTP retornado pela API
     * @param corpoResposta corpo bruto da resposta, possivelmente {@code null}
     * @param erro erro já interpretado, possivelmente {@code null}
     * @since 24/11/2026
     */
    public BcbLimiteJanelaException(String mensagem, Integer codigoStatus, String corpoResposta, ErroBcb erro) {
        super(mensagem, codigoStatus, corpoResposta, erro);
    }
}
