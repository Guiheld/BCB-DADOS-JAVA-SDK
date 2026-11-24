/**
 * SDK Java para as APIs de dados econômicos do Banco Central do Brasil.
 *
 * <p>
 *     O ponto de entrada é {@link solutions.held.bcb.BcbClient}, configurado por
 *     {@link solutions.held.bcb.BcbClientConfig}. A partir dele acessam-se as sub-APIs de séries
 *     temporais (SGS), câmbio (PTAX) e Expectativas (Focus). A biblioteca usa somente o
 *     {@link java.net.http.HttpClient} do JDK e o Jackson em tempo de execução.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
package solutions.held.bcb;
