package solutions.held.bcb.stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;

class JaneladorSgsTest {

    @Test
    void dividir_periodoMaiorQueDezAnos_geraVariasJanelasDentroDoLimite() {
        List<LocalDate[]> janelas =
                JaneladorSgs.dividir(LocalDate.of(2005, 1, 1), LocalDate.of(2020, 12, 31), Period.ofYears(10));
        assertEquals(2, janelas.size());
        assertEquals(LocalDate.of(2005, 1, 1), janelas.get(0)[0]);
        assertEquals(LocalDate.of(2014, 12, 31), janelas.get(0)[1]);
        assertEquals(LocalDate.of(2015, 1, 1), janelas.get(1)[0]);
        assertEquals(LocalDate.of(2020, 12, 31), janelas.get(1)[1]);
        for (LocalDate[] janela : janelas) {
            assertTrue(janela[1].isBefore(janela[0].plusYears(10)), "janela deve ser menor que dez anos");
        }
    }

    @Test
    void dividir_periodoCurto_geraUmaJanela() {
        List<LocalDate[]> janelas =
                JaneladorSgs.dividir(LocalDate.of(2020, 1, 1), LocalDate.of(2021, 12, 31), Period.ofYears(10));
        assertEquals(1, janelas.size());
        assertEquals(LocalDate.of(2020, 1, 1), janelas.get(0)[0]);
        assertEquals(LocalDate.of(2021, 12, 31), janelas.get(0)[1]);
    }

    @Test
    void janelar_concatenaResultadosDeCadaJanelaPreguicosamente() {
        AtomicInteger chamadas = new AtomicInteger();
        BiFunction<LocalDate, LocalDate, List<String>> buscador = (inicio, fim) -> {
            chamadas.incrementAndGet();
            List<String> itens = new ArrayList<>();
            itens.add(inicio.toString());
            itens.add(fim.toString());
            return itens;
        };
        List<String> resultado = JaneladorSgs.janelar(
                        LocalDate.of(2005, 1, 1), LocalDate.of(2020, 12, 31), Period.ofYears(10), buscador)
                .toList();
        assertEquals(2, chamadas.get());
        assertEquals(4, resultado.size());
        assertEquals("2005-01-01", resultado.get(0));
    }
}
