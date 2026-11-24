package solutions.held.bcb.stream;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Paginador preguiçoso para os serviços OData (Focus) do Banco Central.
 *
 * <p>
 *     Percorre páginas via {@code $skip}/{@code $top}, buscando cada página sob demanda e parando
 *     quando uma página devolve menos itens que o tamanho solicitado. Necessário porque o Olinda
 *     ignora {@code $count=true} e não informa o total de registros. É de uso interno da SDK.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class PaginadorOData {

    private PaginadorOData() {}

    /**
     * Cria um {@link Stream} que percorre todas as páginas do recurso.
     *
     * @param <T> tipo dos registros
     * @param tamanhoPagina número de registros solicitados por página
     * @param buscador função que consulta uma página {@code (skip, top)} e devolve seus registros
     * @return um fluxo preguiçoso com os registros de todas as páginas
     * @since 24/11/2026
     */
    public static <T> Stream<T> paginar(Integer tamanhoPagina, BiFunction<Integer, Integer, List<T>> buscador) {
        Iterator<T> iterador = new Iterator<>() {

            private Integer skip = 0;
            private Boolean terminou = false;
            private Iterator<T> atual = Collections.emptyIterator();

            @Override
            public boolean hasNext() {
                while (!this.atual.hasNext() && !this.terminou) {
                    List<T> pagina = buscador.apply(this.skip, tamanhoPagina);
                    this.skip += pagina.size();
                    if (pagina.size() < tamanhoPagina) this.terminou = Boolean.TRUE;
                    this.atual = pagina.iterator();
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
}
