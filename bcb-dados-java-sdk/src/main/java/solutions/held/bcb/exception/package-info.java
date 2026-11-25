/**
 * Hierarquia de exceções da SDK de dados econômicos do Banco Central.
 *
 * <p>
 *     A raiz é {@link solutions.held.bcb.exception.BcbException}, que cobre falhas de rede e de uso
 *     geral. Erros HTTP derivam de {@link solutions.held.bcb.exception.BcbApiException}, ramificando
 *     em {@link solutions.held.bcb.exception.BcbClientException} (4xx) e
 *     {@link solutions.held.bcb.exception.BcbServerException} (5xx). As condições específicas de
 *     limite de requisições (429) e de janela de consulta (406) são representadas por
 *     {@link solutions.held.bcb.exception.BcbRateLimitException} e
 *     {@link solutions.held.bcb.exception.BcbLimiteJanelaException}. Falhas de (de)serialização usam
 *     {@link solutions.held.bcb.exception.BcbSerializationException}.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
package solutions.held.bcb.exception;
