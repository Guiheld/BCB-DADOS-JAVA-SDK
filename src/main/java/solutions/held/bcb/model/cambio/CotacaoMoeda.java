package solutions.held.bcb.model.cambio;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cotação de uma moeda estrangeira (PTAX) em um boletim.
 *
 * <p>
 *     Traz tanto a paridade da moeda em relação ao dólar quanto a cotação em relação ao real, além do
 *     tipo de boletim (abertura, intermediário ou fechamento). Uma consulta por dia pode devolver
 *     vários boletins.
 * </p>
 *
 * @param paridadeCompra paridade de compra em relação ao dólar
 * @param paridadeVenda paridade de venda em relação ao dólar
 * @param cotacaoCompra cotação de compra, em reais por unidade de moeda
 * @param cotacaoVenda cotação de venda, em reais por unidade de moeda
 * @param dataHoraCotacao data e hora da cotação (horário de Brasília)
 * @param tipoBoletim rótulo do boletim (por exemplo, {@code Fechamento PTAX})
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public record CotacaoMoeda(
        BigDecimal paridadeCompra,
        BigDecimal paridadeVenda,
        BigDecimal cotacaoCompra,
        BigDecimal cotacaoVenda,

        @JsonDeserialize(using = DataHoraCotacaoDeserializer.class)
        LocalDateTime dataHoraCotacao,

        String tipoBoletim) {}
