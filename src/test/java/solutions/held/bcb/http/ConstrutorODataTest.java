package solutions.held.bcb.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class ConstrutorODataTest {

    @Test
    void funcao_comParametroData_usaFormatoMesDiaAnoEAspas() {
        String url = ConstrutorOData.funcao("http://base", "CotacaoDolarDia")
                .parametroData("dataCotacao", LocalDate.of(2026, 11, 19))
                .construir();
        assertTrue(url.contains("CotacaoDolarDia(dataCotacao=@dataCotacao)"), url);
        assertTrue(url.contains("@dataCotacao='11-19-2026'"), url);
        assertTrue(url.endsWith("$format=json"), url);
    }

    @Test
    void funcao_comMoedaEData_montaAssinaturaComDoisParametros() {
        String url = ConstrutorOData.funcao("http://base", "CotacaoMoedaDia")
                .parametroTexto("moeda", "EUR")
                .parametroData("dataCotacao", LocalDate.of(2026, 10, 15))
                .filtro("tipoBoletim eq 'Fechamento PTAX'")
                .construir();
        assertTrue(url.contains("CotacaoMoedaDia(moeda=@moeda,dataCotacao=@dataCotacao)"), url);
        assertTrue(url.contains("@moeda='EUR'"), url);
        assertTrue(url.contains("@dataCotacao='10-15-2026'"), url);
        assertTrue(url.contains("$filter=tipoBoletim%20eq%20'Fechamento%20PTAX'"), url);
    }

    @Test
    void entidade_comOpcoes_montaFiltroOrdenacaoTopESelect() {
        String url = ConstrutorOData.entidade("http://base", "ExpectativasMercadoAnuais")
                .filtro("Indicador eq 'IPCA'")
                .ordenarPor("Data desc")
                .top(3)
                .skip(6)
                .selecionar("Indicador", "Data", "Media")
                .construir();
        assertTrue(url.startsWith("http://base/ExpectativasMercadoAnuais?"), url);
        assertTrue(url.contains("$filter=Indicador%20eq%20'IPCA'"), url);
        assertTrue(url.contains("$orderby=Data%20desc"), url);
        assertTrue(url.contains("$top=3"), url);
        assertTrue(url.contains("$skip=6"), url);
        assertTrue(url.contains("$select=Indicador,Data,Media"), url);
        assertTrue(url.contains("$format=json"), url);
    }

    @Test
    void entidade_semOpcoes_apenasFormatoJson() {
        String url = ConstrutorOData.entidade("http://base", "Moedas").construir();
        assertEquals("http://base/Moedas?$format=json", url);
    }
}
