package solutions.held.bcb.stream;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Janelador de consultas ao SGS do Banco Central.
 *
 * <p>
 *     As séries de periodicidade diária do SGS aceitam, por consulta, uma janela de no máximo dez
 *     anos (do contrário a API responde HTTP 406). Este utilitário fatia o período solicitado em
 *     janelas de até a duração informada e expõe os resultados como um {@link Stream} preguiçoso, que
 *     busca cada janela sob demanda e concatena os registros em ordem cronológica. É de uso interno
 *     da SDK.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class JaneladorSgs {

    private JaneladorSgs() {}

    /**
     * Cria um {@link Stream} que percorre o período fatiado em janelas de até a duração informada.
     *
     * @param <T> tipo dos valores da série
     * @param inicio data inicial do período, inclusive
     * @param fim data final do período, inclusive
     * @param janelaMaxima duração máxima de cada janela
     * @param buscador função que consulta uma janela {@code [inicio, fim]} e devolve seus valores
     * @return um fluxo preguiçoso com os valores de todas as janelas
     * @since 24/11/2026
     */
    public static <T> Stream<T> janelar(
            LocalDate inicio, LocalDate fim, Period janelaMaxima, BiFunction<LocalDate, LocalDate, List<T>> buscador) {
        List<LocalDate[]> janelas = new ArrayList<>(JaneladorSgs.dividir(inicio, fim, janelaMaxima));

        Iterator<T> iterador = new Iterator<>() {
            private Integer indiceJanela = 0;
            private Iterator<T> atual = Collections.emptyIterator();

            @Override
            public boolean hasNext() {
                while (!this.atual.hasNext() && this.indiceJanela < janelas.size()) {
                    LocalDate[] janela = janelas.get(this.indiceJanela++);
                    this.atual = buscador.apply(janela[0], janela[1]).iterator();
                }
                return this.atual.hasNext();
            }

            @Override
            public T next() {
                if (!this.hasNext()) throw new java.util.NoSuchElementException();
                return this.atual.next();
            }
        };

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(iterador, Spliterator.ORDERED | Spliterator.NONNULL), false);
    }

    /**
     * Divide um período em janelas sucessivas de até a duração informada.
     *
     * @param inicio data inicial, inclusive
     * @param fim data final, inclusive
     * @param janelaMaxima duração máxima de cada janela
     * @return a lista de janelas, cada uma como par {@code [inicio, fim]}
     * @throws java.lang.IllegalArgumentException quando a data inicial é posterior à final
     * @since 24/11/2026
     */
    static List<LocalDate[]> dividir(LocalDate inicio, LocalDate fim, Period janelaMaxima) {
        if (inicio.isAfter(fim))
            throw new java.lang.IllegalArgumentException("A data inicial não pode ser posterior à data final.");

        List<LocalDate[]> janelas = new ArrayList<>();
        LocalDate cursor = inicio;
        while (!cursor.isAfter(fim)) {
            LocalDate fimJanela = cursor.plus(janelaMaxima).minusDays(1);
            if (fimJanela.isAfter(fim)) fimJanela = fim;
            janelas.add(new LocalDate[] {cursor, fimJanela});
            cursor = fimJanela.plusDays(1);
        }
        return janelas;
    }
}
