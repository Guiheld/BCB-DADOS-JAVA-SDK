package solutions.held.bcb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import solutions.held.bcb.model.focus.ExpectativaMercado;
import solutions.held.bcb.model.focus.ExpectativaTop5Selic;

class FocusApiTest {

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
    void anuais_comFiltrosOrdenacaoETop_montaConsultaEDesserializa() {
        this.stub.responder(
                200,
                "{\"value\":[{\"Indicador\":\"IPCA\",\"Data\":\"2026-11-19\",\"Media\":6.07,\"numeroRespondentes\":40}]}",
                "application/json");
        List<ExpectativaMercado> anuais = this.stub
                .cliente()
                .focus()
                .anuais()
                .indicador("IPCA")
                .ordenarPor("Data", true)
                .top(3)
                .buscar();
        assertEquals(1, anuais.size());
        assertEquals("IPCA", anuais.get(0).indicador());
        String caminho = this.stub.caminhos.get(0);
        assertTrue(caminho.contains("/ExpectativasMercadoAnuais"), caminho);
        assertTrue(caminho.contains("$filter=Indicador%20eq%20'IPCA'"), caminho);
        assertTrue(caminho.contains("$orderby=Data%20desc"), caminho);
        assertTrue(caminho.contains("$top=3"), caminho);
    }

    @Test
    void mensais_usaNomeSingularDoEntitySet() {
        this.stub.responder(200, "{\"value\":[]}", "application/json");
        this.stub.cliente().focus().mensais().buscar();
        assertTrue(this.stub.caminhos.get(0).contains("/ExpectativaMercadoMensais"));
    }

    @Test
    void top5Selic_desserializaCamposMinusculos() {
        this.stub.responder(
                200,
                "{\"value\":[{\"indicador\":\"Selic\",\"Data\":\"2026-11-19\",\"reuniao\":\"R8/2026\",\"tipoCalculo\":\"C\",\"media\":13.74,\"coeficienteVariacao\":0.27}]}",
                "application/json");
        List<ExpectativaTop5Selic> top5 =
                this.stub.cliente().focus().top5Selic().buscar();
        assertEquals(1, top5.size());
        assertEquals("R8/2026", top5.get(0).reuniao());
        assertEquals(new java.math.BigDecimal("0.27"), top5.get(0).coeficienteVariacao());
    }

    @Test
    void stream_paginaViaSkipAtePaginaCurta() {
        this.stub.manipulador = troca -> {
            String caminho = troca.getRequestURI().toString();
            String corpo = caminho.contains("$skip=2")
                    ? "{\"value\":[{\"Indicador\":\"IPCA\",\"Data\":\"2026-11-19\"}]}"
                    : "{\"value\":[{\"Indicador\":\"IPCA\",\"Data\":\"2026-11-19\"},{\"Indicador\":\"IGP-M\",\"Data\":\"2026-11-19\"}]}";
            ServidorStub.escrever(troca, 200, corpo, "application/json");
        };
        List<ExpectativaMercado> todos =
                this.stub.cliente(1, 2).focus().anuais().stream().toList();
        assertEquals(3, todos.size());
        assertTrue(this.stub.caminhos.stream().anyMatch(caminho -> caminho.contains("$skip=2")));
    }
}
