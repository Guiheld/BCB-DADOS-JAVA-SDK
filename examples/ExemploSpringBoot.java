import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import solutions.held.bcb.BcbClient;
import solutions.held.bcb.model.enums.SerieSgs;
import solutions.held.bcb.spring.BcbClientConfigCustomizer;

/**
 * Exemplo ILUSTRATIVO do uso com Spring Boot via {@code bcb-dados-spring-boot-starter}.
 *
 * <p>
 *     Diferente dos demais exemplos, este NÃO roda em modo arquivo-único: pressupõe uma aplicação
 *     Spring Boot (o starter auto-configura o {@link BcbClient}, que é injetado nos serviços). Mostra
 *     injeção, cache do Spring e ajuste programático. Detalhes de configuração, cache e observabilidade
 *     estão no README do módulo {@code bcb-dados-spring-boot-starter}.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 25/11/2026
 */
public final class ExemploSpringBoot {

    private ExemploSpringBoot() {}

    /** Serviço que recebe o {@link BcbClient} por injeção e cacheia uma cotação histórica. */
    @Service
    static class CambioService {

        private final BcbClient bcb;

        CambioService(BcbClient bcb) {
            this.bcb = bcb;
        }

        // A cotação de uma data passada e fechada nunca muda: seguro cachear com TTL longo.
        // A chave inclui o parâmetro que varia (a data).
        @Cacheable(value = "cambio-dolar-dia", key = "#dia.toString()")
        public Optional<BigDecimal> dolarVenda(LocalDate dia) {
            return bcb.cambio().cotacaoDolarDia(dia).map(cotacao -> cotacao.cotacaoVenda());
        }
    }

    /** Serviço SGS com cache por intervalo (chave com todos os parâmetros da consulta). */
    @Service
    static class SelicService {

        private final BcbClient bcb;

        SelicService(BcbClient bcb) {
            this.bcb = bcb;
        }

        @Cacheable(value = "sgs-serie", key = "#serie.name() + ':' + #inicio + ':' + #fim")
        public List<?> serie(SerieSgs serie, LocalDate inicio, LocalDate fim) {
            return bcb.sgs().consultar(serie, inicio, fim);
        }
    }

    /**
     * Habilita o cache do Spring e ajusta a SDK programaticamente — por exemplo, fornecendo um
     * {@code HttpClient} próprio (proxy, executor dedicado ou cliente instrumentado).
     */
    @Configuration
    @EnableCaching
    static class BcbExtrasConfig {

        @Bean
        BcbClientConfigCustomizer bcbCustomizer() {
            // Ex.: builder.httpClient(meuHttpClientInstrumentado);
            return builder -> builder.maxTentativas(5);
        }
    }
}
