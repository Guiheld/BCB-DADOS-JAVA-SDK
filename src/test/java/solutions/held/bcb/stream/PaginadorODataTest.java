package solutions.held.bcb.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class PaginadorODataTest {

    @Test
    void paginar_percorreTodasAsPaginasAtePaginaCurta() {
        List<Integer> dados = List.of(1, 2, 3, 4, 5);
        AtomicInteger chamadas = new AtomicInteger();
        List<Integer> resultado = PaginadorOData.paginar(2, (skip, tamanho) -> {
                    chamadas.incrementAndGet();
                    int ate = Math.min(skip + tamanho, dados.size());
                    return skip >= dados.size() ? List.of() : dados.subList(skip, ate);
                })
                .toList();
        assertEquals(dados, resultado);
        assertEquals(3, chamadas.get());
    }

    @Test
    void paginar_primeiraPaginaVazia_naoPercorreNada() {
        AtomicInteger chamadas = new AtomicInteger();
        List<Integer> resultado = PaginadorOData.paginar(10, (skip, tamanho) -> {
                    chamadas.incrementAndGet();
                    return List.<Integer>of();
                })
                .toList();
        assertEquals(List.of(), resultado);
        assertEquals(1, chamadas.get());
    }
}
