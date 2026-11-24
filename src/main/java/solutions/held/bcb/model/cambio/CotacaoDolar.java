package solutions.held.bcb.model.cambio;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cotação do dólar dos Estados Unidos (PTAX) em um boletim.
 *
 * <p>
 *     Diferentemente da cotação de outras moedas, a cotação do dólar não traz o tipo de boletim nem
 *     a paridade. Os valores de compra e venda são expressos em reais por dólar.
 * </p>
 *
 * @param cotacaoCompra cotação de compra, em reais por dólar
 * @param cotacaoVenda cotação de venda, em reais por dólar
 * @param dataHoraCotacao data e hora da cotação (horário de Brasília)
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public record CotacaoDolar(
        BigDecimal cotacaoCompra,
        BigDecimal cotacaoVenda,

        @JsonDeserialize(using = DataHoraCotacaoDeserializer.class)
        LocalDateTime dataHoraCotacao) {}
