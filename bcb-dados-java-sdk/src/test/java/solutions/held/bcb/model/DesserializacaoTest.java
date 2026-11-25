package solutions.held.bcb.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import solutions.held.bcb.BcbClientConfig;
import solutions.held.bcb.model.cambio.CotacaoDolar;
import solutions.held.bcb.model.focus.ExpectativaMercado;
import solutions.held.bcb.model.focus.ExpectativaTop5Selic;
import solutions.held.bcb.model.sgs.ValorSerie;

class DesserializacaoTest {

    private final ObjectMapper mapper = BcbClientConfig.mapperPadrao();

    @Test
    void valorSerie_dataBrasileiraEValorTextual_viramLocalDateEBigDecimal() throws Exception {
        ValorSerie valor = this.mapper.readValue("{\"data\":\"18/11/2026\",\"valor\":\"0.050788\"}", ValorSerie.class);
        assertEquals(LocalDate.of(2026, 11, 18), valor.data());
        assertEquals(new BigDecimal("0.050788"), valor.valor());
    }

    @Test
    void valorSerie_valorNegativo_eAceito() throws Exception {
        ValorSerie valor = this.mapper.readValue("{\"data\":\"01/08/2026\",\"valor\":\"-0.32\"}", ValorSerie.class);
        assertEquals(new BigDecimal("-0.32"), valor.valor());
    }

    @Test
    void valorSerie_listaJson_desserializa() throws Exception {
        JavaType tipo = this.mapper.getTypeFactory().constructCollectionType(List.class, ValorSerie.class);
        List<ValorSerie> valores = this.mapper.readValue(
                "[{\"data\":\"18/11/2026\",\"valor\":\"5.81\"},{\"data\":\"19/11/2026\",\"valor\":\"5.88\"}]", tipo);
        assertEquals(2, valores.size());
    }

    @Test
    void cotacaoDolar_dataHoraComMicrossegundos_desserializa() throws Exception {
        CotacaoDolar cotacao = this.mapper.readValue(
                "{\"cotacaoCompra\":5.88830,\"cotacaoVenda\":5.88890,\"dataHoraCotacao\":\"2026-11-19 13:04:18.325373\"}",
                CotacaoDolar.class);
        assertEquals(new BigDecimal("5.88890"), cotacao.cotacaoVenda());
        assertEquals(LocalDateTime.of(2026, 11, 19, 13, 4, 18, 325373000), cotacao.dataHoraCotacao());
    }

    @Test
    void cotacaoDolar_dataHoraSemFracao_desserializa() throws Exception {
        CotacaoDolar cotacao = this.mapper.readValue(
                "{\"cotacaoCompra\":5.0,\"cotacaoVenda\":5.1,\"dataHoraCotacao\":\"2026-10-15 13:00:00\"}",
                CotacaoDolar.class);
        assertEquals(LocalDateTime.of(2026, 10, 15, 13, 0, 0), cotacao.dataHoraCotacao());
    }

    @Test
    void expectativaMercado_casingPascalCase_mapeadoComJsonProperty() throws Exception {
        String json =
                "{\"Indicador\":\"IPCA\",\"IndicadorDetalhe\":null,\"Data\":\"2026-11-19\",\"DataReferencia\":\"2026\",\"Media\":6.0706,\"numeroRespondentes\":40,\"baseCalculo\":0,\"@odata.etag\":\"x\"}";
        ExpectativaMercado exp = this.mapper.readValue(json, ExpectativaMercado.class);
        assertEquals("IPCA", exp.indicador());
        assertEquals(LocalDate.of(2026, 11, 19), exp.data());
        assertEquals("2026", exp.dataReferencia());
        assertEquals(new BigDecimal("6.0706"), exp.media());
        assertEquals(40, exp.numeroRespondentes());
        assertNull(exp.indicadorDetalhe());
    }

    @Test
    void expectativaTop5Selic_casingMinusculoComCoeficiente_mapeado() throws Exception {
        String json =
                "{\"indicador\":\"Selic\",\"Data\":\"2026-11-19\",\"reuniao\":\"R8/2026\",\"tipoCalculo\":\"C\",\"media\":13.7442,\"mediana\":13.75,\"desvioPadrao\":0.0377,\"coeficienteVariacao\":0.2743,\"minimo\":13.5,\"maximo\":13.75}";
        ExpectativaTop5Selic exp = this.mapper.readValue(json, ExpectativaTop5Selic.class);
        assertEquals("Selic", exp.indicador());
        assertEquals("R8/2026", exp.reuniao());
        assertEquals(new BigDecimal("0.2743"), exp.coeficienteVariacao());
        assertEquals(LocalDate.of(2026, 11, 19), exp.data());
    }

    @Test
    void campoDesconhecido_eIgnorado() throws Exception {
        ValorSerie valor = this.mapper.readValue(
                "{\"data\":\"18/11/2026\",\"valor\":\"5.81\",\"campoNovo\":123}", ValorSerie.class);
        assertTrue(valor.valor().compareTo(BigDecimal.ZERO) > 0);
    }
}
