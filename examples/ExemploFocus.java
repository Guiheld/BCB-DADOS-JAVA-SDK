import solutions.held.bcb.BcbClient;

/**
 * Exemplo de uso da sub-API de Expectativas/Focus.
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class ExemploFocus {

    private ExemploFocus() {}

    /**
     * Executa o exemplo.
     *
     * @param args não utilizados
     * @since 24/11/2026
     */
    public static void main(String[] args) {
        BcbClient bcb = BcbClient.criar();

        System.out.println("== Expectativas anuais de IPCA (5 coletas mais recentes) ==");
        bcb.focus()
                .anuais()
                .indicador("IPCA")
                .ordenarPor("Data", true)
                .top(5)
                .buscar()
                .forEach(expectativa -> System.out.printf(
                        "%s (ref %s): mediana %s, %d respondentes%n",
                        expectativa.data(),
                        expectativa.dataReferencia(),
                        expectativa.mediana(),
                        expectativa.numeroRespondentes()));
    }
}
