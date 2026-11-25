package solutions.held.bcb.exception;

import java.io.Serial;

/**
 * Exceção lançada quando ocorre falha ao serializar ou desserializar dados das APIs do Banco Central.
 *
 * <p>
 *     Normalmente indica que o corpo recebido não correspondeu ao modelo esperado. Preserva a causa
 *     original para diagnóstico. Relate se alguma tipagem não estiver de acordo!
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public class BcbSerializationException extends BcbException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção de serialização.
     *
     * @param mensagem descrição do erro ocorrido
     * @param causa exceção que originou esta falha
     * @since 24/11/2026
     */
    public BcbSerializationException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
