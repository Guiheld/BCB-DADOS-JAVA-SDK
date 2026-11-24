package solutions.held.bcb.http;

import solutions.held.bcb.exception.BcbException;

/**
 * Limitador de vazão (throttle) client-side das requisições às APIs do Banco Central.
 *
 * <p>
 *     Garante um intervalo mínimo entre requisições consecutivas, derivado da vazão configurada em
 *     requisições por segundo. É de uso interno da SDK e seguro para uso concorrente. Uma vazão menor
 *     ou igual a zero desliga o limitador.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 24/11/2026
 */
public final class Throttler {

    private final Long intervaloNanos;
    private long proximoInstantePermitidoNanos;

    /**
     * Cria o limitador para a vazão informada.
     *
     * @param vazaoPorSegundo número máximo de requisições por segundo; {@code <= 0} desliga o limite
     * @since 24/11/2026
     */
    public Throttler(Double vazaoPorSegundo) {
        this.intervaloNanos = vazaoPorSegundo <= 0 ? 0L : (long) (1_000_000_000.0 / vazaoPorSegundo);
        this.proximoInstantePermitidoNanos = System.nanoTime();
    }

    /**
     * Bloqueia a thread corrente, se necessário, até que uma nova requisição possa ser emitida sem
     * violar a vazão configurada.
     *
     * @since 24/11/2026
     */
    public synchronized void adquirir() {
        if (this.intervaloNanos == 0L) return;

        long agora = java.lang.System.nanoTime();
        long esperaNanos = this.proximoInstantePermitidoNanos - agora;
        if (esperaNanos > 0) {
            try {
                Thread.sleep(esperaNanos / 1_000_000L, (int) (esperaNanos % 1_000_000L));
            } catch (java.lang.InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BcbException("Espera por vazão de requisições ao Banco Central interrompida.", e);
            }
        }
        this.proximoInstantePermitidoNanos = Math.max(agora, this.proximoInstantePermitidoNanos) + this.intervaloNanos;
    }
}
