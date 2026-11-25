package solutions.held.bcb.spring;

import java.util.function.Consumer;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import solutions.held.bcb.BcbClient;
import solutions.held.bcb.BcbClientConfig;

/**
 * Auto-configuração Spring Boot da SDK de dados econômicos do Banco Central.
 *
 * <p>
 *     Com a SDK no classpath, expõe um {@link BcbClientConfig} montado a partir das propriedades
 *     {@code bcb.*} (ver {@link BcbProperties}) e um {@link BcbClient} pronto para injeção. Ambos os
 *     beans recuam se a aplicação já declarar o seu próprio equivalente ({@link ConditionalOnMissingBean}).
 *     Ajustes programáticos (por exemplo, um {@code HttpClient} instrumentado) são feitos via
 *     {@link BcbClientConfigCustomizer}.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 25/11/2026
 */
@AutoConfiguration
@ConditionalOnClass(BcbClient.class)
@EnableConfigurationProperties(BcbProperties.class)
public class BcbAutoConfiguration {

    /**
     * Monta a configuração da SDK a partir das propriedades e dos customizadores.
     *
     * @param propriedades propriedades {@code bcb.*} (apenas os campos preenchidos sobrescrevem os padrões)
     * @param customizadores ajustes programáticos opcionais, aplicados por último
     * @return a configuração imutável da SDK
     * @since 25/11/2026
     */
    @Bean
    @ConditionalOnMissingBean
    public BcbClientConfig bcbClientConfig(
            BcbProperties propriedades, ObjectProvider<BcbClientConfigCustomizer> customizadores) {
        BcbClientConfig.Builder construtor = BcbClientConfig.builder();
        aplicar(propriedades.urlBaseSgs(), construtor::urlBaseSgs);
        aplicar(propriedades.urlBasePtax(), construtor::urlBasePtax);
        aplicar(propriedades.urlBaseExpectativas(), construtor::urlBaseExpectativas);
        aplicar(propriedades.timeoutConexao(), construtor::timeoutConexao);
        aplicar(propriedades.timeoutResposta(), construtor::timeoutResposta);
        aplicar(propriedades.maxTentativas(), construtor::maxTentativas);
        aplicar(propriedades.backoffInicial(), construtor::backoffInicial);
        aplicar(propriedades.backoffMaximo(), construtor::backoffMaximo);
        aplicar(propriedades.multiplicadorBackoff(), construtor::multiplicadorBackoff);
        aplicar(propriedades.vazaoPorSegundo(), construtor::vazaoPorSegundo);
        aplicar(propriedades.janelaMaximaSgs(), construtor::janelaMaximaSgs);
        aplicar(propriedades.tamanhoPaginaFocus(), construtor::tamanhoPaginaFocus);
        aplicar(propriedades.userAgent(), construtor::userAgent);
        customizadores.orderedStream().forEach(customizador -> customizador.customize(construtor));
        return construtor.build();
    }

    /**
     * Cria o {@link BcbClient} a partir da configuração resolvida.
     *
     * @param config a configuração da SDK
     * @return o cliente pronto para uso
     * @since 25/11/2026
     */
    @Bean
    @ConditionalOnMissingBean
    public BcbClient bcbClient(BcbClientConfig config) {
        return BcbClient.comConfig(config);
    }

    private static <T> void aplicar(T valor, Consumer<T> ajuste) {
        if (valor != null) {
            ajuste.accept(valor);
        }
    }
}
