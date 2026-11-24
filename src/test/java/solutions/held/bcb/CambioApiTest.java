package solutions.held.bcb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import solutions.held.bcb.model.cambio.CotacaoDolar;

class CambioApiTest {

    private ServidorStub stub;

    @BeforeEach
    void iniciar() throws IOException {
        this.stub = new ServidorStub();
    }

    @AfterEach
    void parar() {
        this.stub.close();
    }

    @Test
    void cotacaoDolarDia_comDados_devolveCotacaoEUsaFormatoMesDiaAno() {
        this.stub.responder(
                200,
                "{\"value\":[{\"cotacaoCompra\":5.8883,\"cotacaoVenda\":5.8889,\"dataHoraCotacao\":\"2026-11-19 13:04:18.3\"}]}",
                "application/json");
        Optional<CotacaoDolar> cotacao = this.stub.cliente().cambio().cotacaoDolarDia(LocalDate.of(2026, 11, 19));
        assertTrue(cotacao.isPresent());
        assertEquals(new java.math.BigDecimal("5.8889"), cotacao.get().cotacaoVenda());
        assertTrue(this.stub.caminhos.get(0).contains("CotacaoDolarDia(dataCotacao=@dataCotacao)"));
        assertTrue(this.stub.caminhos.get(0).contains("@dataCotacao='11-19-2026'"));
    }

    @Test
    void cotacaoDolarDia_diaSemCotacao_devolveVazio() {
        this.stub.responder(200, "{\"value\":[]}", "application/json");
        Optional<CotacaoDolar> cotacao = this.stub.cliente().cambio().cotacaoDolarDia(LocalDate.of(2026, 11, 20));
        assertFalse(cotacao.isPresent());
    }

    @Test
    void cotacaoMoedaDiaFechamento_aplicaFiltroDeBoletim() {
        this.stub.responder(200, "{\"value\":[]}", "application/json");
        this.stub.cliente().cambio().cotacaoMoedaDiaFechamento("EUR", LocalDate.of(2026, 11, 19));
        assertTrue(this.stub.caminhos.get(0).contains("$filter=tipoBoletim%20eq%20'Fechamento%20PTAX'"));
        assertTrue(this.stub.caminhos.get(0).contains("@moeda='EUR'"));
    }

    @Test
    void moedas_consultaEntitySet() {
        this.stub.responder(
                200,
                "{\"value\":[{\"simbolo\":\"EUR\",\"nomeFormatado\":\"Euro\",\"tipoMoeda\":\"B\"}]}",
                "application/json");
        this.stub.cliente().cambio().moedas();
        assertTrue(this.stub.caminhos.get(0).contains("/Moedas"));
    }
}
