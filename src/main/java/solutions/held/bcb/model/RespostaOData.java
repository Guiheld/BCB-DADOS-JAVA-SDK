package solutions.held.bcb.model;

import java.util.List;

/**
 * Envelope padrão das respostas OData (v4) dos serviços Olinda do Banco Central.
 *
 * <p>
 *     As APIs de câmbio (PTAX) e de Expectativas (Focus) entregam os registros dentro do campo
 *     {@code value}; os demais campos de metadados (como {@code @odata.context}) são ignorados pela
 *     configuração tolerante do {@code ObjectMapper}. Um dia sem dados devolve {@code value} vazio.
 * </p>
 *
 * @param <T> tipo dos registros contidos na resposta
 * @param value lista de registros devolvida pelo serviço, possivelmente vazia
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public record RespostaOData<T>(List<T> value) {}
