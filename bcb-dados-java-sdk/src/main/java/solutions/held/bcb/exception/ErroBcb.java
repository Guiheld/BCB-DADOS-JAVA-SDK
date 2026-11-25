package solutions.held.bcb.exception;

import java.util.Optional;

/**
 * Representação tolerante do corpo de erro retornado pelas APIs do Banco Central.
 *
 * <p>
 *     Os serviços do Banco Central não adotam um formato único de erro: o SGS devolve
 *     {@code text/plain} para código inexistente e JSON com os campos {@code error}, {@code message}
 *     e {@code syntax} no estouro da janela de consulta; os serviços Olinda (câmbio e Focus) devolvem
 *     JSON com {@code codigo} e {@code mensagem}, por vezes embrulhado em comentário. Este registro
 *     acomoda todas essas variações, deixando nulos os campos ausentes e preservando o texto bruto
 *     quando o corpo não é um JSON reconhecido.
 * </p>
 *
 * @param error mensagem curta de erro do SGS, ou {@code null}
 * @param message mensagem detalhada do SGS, ou {@code null}
 * @param syntax exemplo de sintaxe sugerido pelo SGS, ou {@code null}
 * @param codigo código numérico de erro dos serviços Olinda, ou {@code null}
 * @param mensagem mensagem de erro dos serviços Olinda, ou {@code null}
 * @param textoBruto corpo bruto preservado quando não é JSON reconhecido, ou {@code null}
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public record ErroBcb(String error, String message, String syntax, Integer codigo, String mensagem, String textoBruto) {

    /**
     * Devolve a mensagem de erro mais legível disponível.
     *
     * @return a primeira mensagem não vazia dentre {@code message}, {@code mensagem}, {@code error} e
     *     {@code textoBruto}, ou vazio quando nenhuma existe
     * @since 24/11/2026
     */
    @SuppressWarnings("unused")
    public Optional<String> mensagemLegivel() {
        for (String candidato : new String[] {this.message, this.mensagem, this.error, this.textoBruto}) {
            if (candidato != null && !candidato.isBlank()) return Optional.of(candidato.strip());
        }
        return Optional.empty();
    }
}
