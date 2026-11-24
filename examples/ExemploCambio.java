import java.time.LocalDate;
import solutions.held.bcb.BcbClient;

/**
 * Exemplo de uso da sub-API de câmbio (PTAX).
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class ExemploCambio {

    private ExemploCambio() {}

    /**
     * Executa o exemplo.
     *
     * @param args não utilizados
     * @since 24/11/2026
     */
    public static void main(String[] args) {
        BcbClient bcb = BcbClient.criar();

        System.out.println("== Cotação do dólar em 19/11/2026 ==");
        bcb.cambio()
                .cotacaoDolarDia(LocalDate.of(2026, 11, 19))
                .ifPresentOrElse(
                        cotacao -> System.out.printf(
                                "Compra: %s | Venda: %s | %s%n",
                                cotacao.cotacaoCompra(), cotacao.cotacaoVenda(), cotacao.dataHoraCotacao()),
                        () -> System.out.println("Sem cotação na data (fim de semana, feriado ou data futura)."));

        System.out.println("== Primeiras 5 moedas disponíveis ==");
        bcb.cambio().moedas().stream()
                .limit(5)
                .forEach(moeda -> System.out.printf("%s - %s%n", moeda.simbolo(), moeda.nomeFormatado()));
    }
}
