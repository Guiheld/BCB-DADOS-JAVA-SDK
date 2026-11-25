package solutions.held.bcb.model.cambio;

/**
 * Moeda disponível no serviço de câmbio PTAX.
 *
 * <p>
 *     O símbolo é o código de três letras usado nas consultas de cotação de moeda (por exemplo,
 *     {@code EUR}). O tipo indica a forma de expressão da paridade ("A" ou "B").
 * </p>
 *
 * @param simbolo código de três letras da moeda
 * @param nomeFormatado nome legível da moeda
 * @param tipoMoeda código do tipo de paridade ("A" ou "B")
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public record Moeda(String simbolo, String nomeFormatado, String tipoMoeda) {}
