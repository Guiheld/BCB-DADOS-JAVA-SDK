package solutions.held.bcb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import solutions.held.bcb.exception.BcbClientException;
import solutions.held.bcb.exception.BcbException;
import solutions.held.bcb.exception.BcbLimiteJanelaException;
import solutions.held.bcb.exception.BcbRateLimitException;
import solutions.held.bcb.exception.BcbServerException;
import solutions.held.bcb.http.HttpTransport;
import solutions.held.bcb.model.cambio.CotacaoDolar;
import solutions.held.bcb.model.sgs.ValorSerie;

class HttpTransportTest {

    private ServidorStub stub;

    @BeforeEach
    void iniciar() throws IOException {
        this.stub = new ServidorStub();
    }

    @AfterEach
    void parar() {
        this.stub.close();
    }

    private HttpTransport transporte(int maxTentativas) {
        return new HttpTransport(BcbClientConfig.builder()
                .urlBaseSgs(this.stub.urlBase())
                .urlBasePtax(this.stub.urlBase())
                .urlBaseExpectativas(this.stub.urlBase())
                .vazaoPorSegundo(0.0)
                .maxTentativas(maxTentativas)
                .backoffInicial(Duration.ofMillis(1))
                .backoffMaximo(Duration.ofMillis(5))
                .build());
    }

    @Test
    void obterListaSgs_resposta200_desserializaArray() {
        this.stub.responder(200, "[{\"data\":\"18/11/2026\",\"valor\":\"5.81\"}]", "application/json; charset=utf-8");
        List<ValorSerie> valores = this.transporte(1).obterListaSgs(this.stub.urlBase() + "/x", ValorSerie.class);
        assertEquals(1, valores.size());
    }

    @Test
    void obterListaSgs_resposta204_listaVazia() {
        this.stub.responder(204, "", "application/json");
        List<ValorSerie> valores = this.transporte(1).obterListaSgs(this.stub.urlBase() + "/x", ValorSerie.class);
        assertTrue(valores.isEmpty());
    }

    @Test
    void obterListaSgs_404TextoPuro_lancaClientExceptionComTextoBruto() {
        this.stub.responder(404, "A serie nao existe!", "text/plain; charset=utf-8");
        BcbClientException excecao = assertThrows(
                BcbClientException.class,
                () -> this.transporte(1).obterListaSgs(this.stub.urlBase() + "/x", ValorSerie.class));
        assertEquals(404, excecao.codigoStatus());
        assertTrue(excecao.corpoResposta().contains("A serie nao existe!"));
        assertTrue(excecao.erro().isPresent());
        assertEquals("A serie nao existe!", excecao.erro().get().textoBruto());
    }

    @Test
    void obterListaSgs_406Json_lancaLimiteJanelaComMensagem() {
        String corpo =
                "{\"error\":\"janela maxima de 10 anos\",\"message\":\"informe a dataInicial\",\"syntax\":\"...\"}";
        this.stub.responder(406, corpo, "application/json; charset=utf-8");
        BcbLimiteJanelaException excecao = assertThrows(
                BcbLimiteJanelaException.class,
                () -> this.transporte(1).obterListaSgs(this.stub.urlBase() + "/x", ValorSerie.class));
        assertEquals(406, excecao.codigoStatus());
        assertTrue(excecao.erro().isPresent());
        assertEquals("informe a dataInicial", excecao.erro().get().message());
    }

    @Test
    void obterListaSgs_429_lancaRateLimit() {
        this.stub.responder(429, "<html>Too Many Requests</html>", "text/html");
        BcbRateLimitException excecao = assertThrows(
                BcbRateLimitException.class,
                () -> this.transporte(1).obterListaSgs(this.stub.urlBase() + "/x", ValorSerie.class));
        assertEquals(429, excecao.codigoStatus());
    }

    @Test
    void obterListaSgs_500Repetido_retentaELancaServerException() {
        AtomicInteger chamadas = new AtomicInteger();
        this.stub.manipulador = troca -> {
            chamadas.incrementAndGet();
            ServidorStub.escrever(troca, 500, "{\"codigo\":500,\"mensagem\":\"Erro\"}", "application/json");
        };
        assertThrows(
                BcbServerException.class,
                () -> this.transporte(3).obterListaSgs(this.stub.urlBase() + "/x", ValorSerie.class));
        assertEquals(3, chamadas.get());
    }

    @Test
    void obterListaSgs_500DepoisDe200_recuperaComRetentativa() {
        AtomicInteger chamadas = new AtomicInteger();
        this.stub.manipulador = troca -> {
            if (chamadas.getAndIncrement() == 0) {
                ServidorStub.escrever(troca, 500, "erro", "text/plain");
            } else {
                ServidorStub.escrever(troca, 200, "[{\"data\":\"18/11/2026\",\"valor\":\"5.81\"}]", "application/json");
            }
        };
        List<ValorSerie> valores = this.transporte(3).obterListaSgs(this.stub.urlBase() + "/x", ValorSerie.class);
        assertEquals(1, valores.size());
        assertEquals(2, chamadas.get());
    }

    @Test
    void obterListaOData_erroOlindaEmbrulhadoEmComentario_eInterpretado() {
        String corpo = "/*{ \"codigo\" : 400, \"mensagem\" : \"EntitySet inexistente\" }*/";
        this.stub.responder(400, corpo, "");
        BcbClientException excecao = assertThrows(
                BcbClientException.class,
                () -> this.transporte(1).obterListaOData(this.stub.urlBase() + "/x", CotacaoDolar.class));
        assertEquals(400, excecao.codigoStatus());
        assertTrue(excecao.erro().isPresent());
        assertEquals(400, excecao.erro().get().codigo());
        assertEquals("EntitySet inexistente", excecao.erro().get().mensagem());
    }

    @Test
    void obterListaOData_envelopeComValue_extraiRegistros() {
        String corpo =
                "{\"@odata.context\":\"ctx\",\"value\":[{\"cotacaoCompra\":5.0,\"cotacaoVenda\":5.1,\"dataHoraCotacao\":\"2026-11-19 13:00:00\"}]}";
        this.stub.responder(200, corpo, "application/json");
        List<CotacaoDolar> cotacoes =
                this.transporte(1).obterListaOData(this.stub.urlBase() + "/x", CotacaoDolar.class);
        assertEquals(1, cotacoes.size());
    }

    @Test
    void obterListaSgs_comHttpClientInjetado_usaEsseCliente() {
        HttpClient clienteComProxyMorto = HttpClient.newBuilder()
                .proxy(ProxySelector.of(new InetSocketAddress("127.0.0.1", 1)))
                .connectTimeout(Duration.ofMillis(500))
                .build();
        HttpTransport transporte = new HttpTransport(BcbClientConfig.builder()
                .urlBaseSgs(this.stub.urlBase())
                .urlBasePtax(this.stub.urlBase())
                .urlBaseExpectativas(this.stub.urlBase())
                .vazaoPorSegundo(0.0)
                .maxTentativas(1)
                .backoffInicial(Duration.ofMillis(1))
                .backoffMaximo(Duration.ofMillis(5))
                .httpClient(clienteComProxyMorto)
                .build());
        this.stub.responder(200, "[]", "application/json");
        assertThrows(BcbException.class, () -> transporte.obterListaSgs(this.stub.urlBase() + "/x", ValorSerie.class));
    }
}
