import java.time.LocalDate;
import solutions.held.bcb.BcbClient;
import solutions.held.bcb.model.enums.SerieSgs;

/**
 * Exemplo de uso da sub-API SGS (séries temporais).
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class ExemploSgs {

    private ExemploSgs() {}

    /**
     * Executa o exemplo.
     *
     * @param args não utilizados
     * @since 24/11/2026
     */
    public static void main(String[] args) {
        BcbClient bcb = BcbClient.criar();

        System.out.println("== Últimos 5 valores da meta Selic (Copom) ==");
        bcb.sgs().ultimos(SerieSgs.SELIC_META, 5).forEach(valor -> System.out.printf("%s: %s%n", valor.data(), valor.valor()));

        System.out.println("== Selic diária 2005-2020 (fatiada automaticamente em janelas de 10 anos) ==");
        long total = bcb.sgs()
                .stream(SerieSgs.SELIC_DIARIA, LocalDate.of(2005, 1, 1), LocalDate.of(2020, 12, 31))
                .count();
        System.out.println("Total de registros: " + total);
    }
}
