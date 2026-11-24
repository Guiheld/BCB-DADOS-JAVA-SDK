package solutions.held.bcb.exception;

import java.io.Serial;

/**
 * Exceção base da SDK de dados econômicos do Banco Central.
 *
 * <p>
 *     Representa qualquer falha originada pela SDK, seja de comunicação de rede, de interpretação
 *     de respostas ou de uso indevido. As demais exceções da biblioteca estendem esta classe,
 *     de modo que as capturar trata qualquer erro produzido pela SDK.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public class BcbException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Cria a exceção com uma mensagem descritiva.
     *
     * @param mensagem descrição do erro ocorrido
     * @since 24/11/2026
     */
    public BcbException(String mensagem) {
        super(mensagem);
    }

    /**
     * Cria a exceção com uma mensagem descritiva e a causa original.
     *
     * @param mensagem descrição do erro ocorrido
     * @param causa exceção que originou esta falha
     * @since 24/11/2026
     */
    public BcbException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
