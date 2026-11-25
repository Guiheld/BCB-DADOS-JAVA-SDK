package solutions.held.bcb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import solutions.held.bcb.model.enums.Periodicidade;
import solutions.held.bcb.model.enums.SerieSgs;
import solutions.held.bcb.model.sgs.ValorSerie;

class SgsApiTest {

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
    void ultimos_montaCaminhoComCodigoEQuantidade() {
        this.stub.responder(200, "[{\"data\":\"07/12/2026\",\"valor\":\"13.75\"}]", "application/json");
        List<ValorSerie> valores = this.stub.cliente().sgs().ultimos(SerieSgs.SELIC_META, 3);
        assertEquals(1, valores.size());
        assertEquals(new java.math.BigDecimal("13.75"), valores.get(0).valor());
        assertTrue(this.stub.caminhos.get(0).contains("/bcdata.sgs.432/dados/ultimos/3"));
    }

    @Test
    void consultar_serieDiariaMaiorQueDezAnos_fatiaEmJanelas() {
        this.stub.responder(200, "[{\"data\":\"02/01/2020\",\"valor\":\"0.01\"}]", "application/json");
        List<ValorSerie> valores = this.stub
                .cliente()
                .sgs()
                .consultar(SerieSgs.SELIC_DIARIA, LocalDate.of(2005, 1, 1), LocalDate.of(2020, 12, 31));
        assertEquals(2, this.stub.caminhos.size());
        assertEquals(2, valores.size());
        assertTrue(this.stub.caminhos.get(0).contains("dataInicial=01/01/2005"));
        assertTrue(this.stub.caminhos.get(0).contains("dataFinal=31/12/2014"));
    }

    @Test
    void consultar_serieMensal_naoFatia() {
        this.stub.responder(200, "[{\"data\":\"01/01/2000\",\"valor\":\"0.62\"}]", "application/json");
        this.stub
                .cliente()
                .sgs()
                .consultar(433, Periodicidade.MENSAL, LocalDate.of(2000, 1, 1), LocalDate.of(2015, 12, 31));
        assertEquals(1, this.stub.caminhos.size());
        assertTrue(this.stub.caminhos.get(0).contains("/bcdata.sgs.433/dados"));
    }
}
