package solutions.held.bcb;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servidor HTTP de teste baseado no {@code com.sun.net.httpserver.HttpServer} do JDK.
 *
 * <p>
 *     Permite definir, por cenário, a resposta a ser devolvida e registra os caminhos recebidos para
 *     verificação das URLs construídas pela SDK. Não usa bibliotecas externas de stub.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
final class ServidorStub implements AutoCloseable {

    private final HttpServer servidor;
    volatile HttpHandler manipulador;
    final List<String> caminhos = Collections.synchronizedList(new ArrayList<>());

    ServidorStub() throws IOException {
        this.servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        this.servidor.createContext("/", troca -> {
            this.caminhos.add(troca.getRequestURI().toString());
            this.manipulador.handle(troca);
        });
        this.servidor.start();
    }

    int porta() {
        return this.servidor.getAddress().getPort();
    }

    String urlBase() {
        return "http://localhost:" + this.porta();
    }

    void responder(int status, String corpo, String tipo) {
        this.manipulador = troca -> escrever(troca, status, corpo, tipo);
    }

    BcbClient cliente() {
        return this.cliente(1, 1000);
    }

    BcbClient cliente(@SuppressWarnings("SameParameterValue") int maxTentativas, int tamanhoPaginaFocus) {
        return BcbClient.comConfig(BcbClientConfig.builder()
                .urlBaseSgs(this.urlBase())
                .urlBasePtax(this.urlBase())
                .urlBaseExpectativas(this.urlBase())
                .vazaoPorSegundo(0.0)
                .maxTentativas(maxTentativas)
                .backoffInicial(Duration.ofMillis(1))
                .backoffMaximo(Duration.ofMillis(5))
                .tamanhoPaginaFocus(tamanhoPaginaFocus)
                .build());
    }

    static void escrever(HttpExchange troca, int status, String corpo, String tipo) throws IOException {
        byte[] bytes = corpo.getBytes(StandardCharsets.UTF_8);
        troca.getResponseHeaders().add("Content-Type", tipo);
        if (status == 204 || bytes.length == 0) {
            troca.sendResponseHeaders(status, -1);
            troca.close();
            return;
        }
        troca.sendResponseHeaders(status, bytes.length);
        try (OutputStream saida = troca.getResponseBody()) {
            saida.write(bytes);
        }
    }

    @Override
    public void close() {
        this.servidor.stop(0);
    }
}
