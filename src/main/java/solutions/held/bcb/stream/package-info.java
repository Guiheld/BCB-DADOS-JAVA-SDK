/**
 * Utilitários de fluxo (streaming) da SDK de dados econômicos do Banco Central.
 *
 * <p>
 *     Contém o janelador de séries diárias do SGS ({@link solutions.held.bcb.stream.JaneladorSgs}),
 *     que respeita o limite de dez anos por consulta, e o paginador dos serviços OData
 *     ({@link solutions.held.bcb.stream.PaginadorOData}). Ambos expõem {@link java.util.stream.Stream}
 *     preguiçosos e são de uso interno da SDK.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
package solutions.held.bcb.stream;
