package solutions.held.bcb.spring;

import java.time.Duration;
import java.time.Period;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propriedades de configuração da SDK do Banco Central, ligadas ao prefixo {@code bcb} no
 * {@code application.properties}/{@code application.yml}.
 *
 * <p>
 *     Todos os campos são opcionais: quando ausentes, prevalecem os padrões da própria SDK (definidos
 *     em {@code BcbClientConfig.Builder}). Preencha apenas o que quiser sobrescrever. Durações aceitam
 *     o formato do Spring (ex.: {@code 30s}, {@code 500ms}) e a janela do SGS aceita período (ex.:
 *     {@code 10y}).
 * </p>
 *
 * @param urlBaseSgs endereço base do serviço SGS
 * @param urlBasePtax endereço base do serviço de câmbio PTAX
 * @param urlBaseExpectativas endereço base do serviço de Expectativas/Focus
 * @param timeoutConexao tempo limite de conexão
 * @param timeoutResposta tempo limite de resposta por requisição
 * @param maxTentativas número máximo de tentativas por requisição
 * @param backoffInicial atraso inicial do backoff entre tentativas
 * @param backoffMaximo atraso máximo do backoff entre tentativas
 * @param multiplicadorBackoff multiplicador do backoff exponencial
 * @param vazaoPorSegundo vazão máxima de requisições por segundo (0 ou negativo desliga o limitador)
 * @param janelaMaximaSgs janela máxima das séries diárias do SGS (ex.: {@code 10y})
 * @param tamanhoPaginaFocus tamanho de página da paginação automática do Focus
 * @param userAgent identificação enviada no cabeçalho {@code User-Agent}
 * @author Guilherme A. Held
 * @since 25/11/2026
 */
@ConfigurationProperties(prefix = "bcb")
public record BcbProperties(
        String urlBaseSgs,
        String urlBasePtax,
        String urlBaseExpectativas,
        Duration timeoutConexao,
        Duration timeoutResposta,
        Integer maxTentativas,
        Duration backoffInicial,
        Duration backoffMaximo,
        Double multiplicadorBackoff,
        Double vazaoPorSegundo,
        Period janelaMaximaSgs,
        Integer tamanhoPaginaFocus,
        String userAgent) {}
