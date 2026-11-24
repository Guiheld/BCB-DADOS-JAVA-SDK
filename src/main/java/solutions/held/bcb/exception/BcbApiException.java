package solutions.held.bcb.exception;

import java.io.Serial;
import java.util.Optional;

/**
 * Exceção base para respostas de erro das APIs do Banco Central.
 *
 * <p>
 *     Carrega o código de status HTTP, o corpo bruto da resposta e, quando possível, o erro já
 *     interpretado ({@link ErroBcb}). É estendida pelas exceções específicas de erro de cliente
 *     ({@link BcbClientException}) e de servidor ({@link BcbServerException}).
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public class BcbApiException extends BcbException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Integer codigoStatus;
    private final transient String corpoResposta;
    private final transient ErroBcb erro;

    /**
     * Cria a exceção de erro de API.
     *
     * @param mensagem descrição do erro ocorrido
     * @param codigoStatus código de status HTTP retornado pela API
     * @param corpoResposta corpo bruto da resposta, possivelmente {@code null}
     * @param erro erro já interpretado, possivelmente {@code null}
     * @since 24/11/2026
     */
    public BcbApiException(String mensagem, Integer codigoStatus, String corpoResposta, ErroBcb erro) {
        super(mensagem);
        this.codigoStatus = codigoStatus;
        this.corpoResposta = corpoResposta;
        this.erro = erro;
    }

    /**
     * Devolve o código de status HTTP associado ao erro.
     *
     * @return o código de status HTTP
     * @since 24/11/2026
     */
    public Integer codigoStatus() {
        return this.codigoStatus;
    }

    /**
     * Devolve o corpo bruto da resposta de erro.
     *
     * @return o corpo bruto da resposta, possivelmente {@code null}
     * @since 24/11/2026
     */
    public String corpoResposta() {
        return this.corpoResposta;
    }

    /**
     * Devolve o erro já interpretado, quando foi possível interpretá-lo.
     *
     * @return o erro interpretado, ou vazio quando indisponível
     * @since 24/11/2026
     */
    public Optional<ErroBcb> erro() {
        return Optional.ofNullable(this.erro);
    }
}
