package solutions.held.bcb.spring;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import solutions.held.bcb.BcbClient;
import solutions.held.bcb.BcbClientConfig;

class BcbAutoConfiguracaoTest {

    private final ApplicationContextRunner runner =
            new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(BcbAutoConfiguration.class));

    @Test
    void criaBcbClientEConfigPorPadrao() {
        runner.run(contexto -> {
            assertThat(contexto).hasSingleBean(BcbClient.class);
            assertThat(contexto).hasSingleBean(BcbClientConfig.class);
        });
    }

    @Test
    void aplicaPropriedadesDeclaradas() {
        runner.withPropertyValues("bcb.max-tentativas=7", "bcb.user-agent=meu-agente/1.0")
                .run(contexto -> {
                    BcbClientConfig config = contexto.getBean(BcbClientConfig.class);
                    assertThat(config.maxTentativas()).isEqualTo(7);
                    assertThat(config.userAgent()).isEqualTo("meu-agente/1.0");
                });
    }

    @Test
    void customizadorTemAPalavraFinal() {
        BcbClientConfigCustomizer customizador = construtor -> construtor.maxTentativas(9);
        runner.withPropertyValues("bcb.max-tentativas=7")
                .withBean(BcbClientConfigCustomizer.class, () -> customizador)
                .run(contexto -> assertThat(
                                contexto.getBean(BcbClientConfig.class).maxTentativas())
                        .isEqualTo(9));
    }

    @Test
    void respeitaBcbClientDoUsuario() {
        BcbClient proprio = BcbClient.criar();
        runner.withBean("bcbClientProprio", BcbClient.class, () -> proprio)
                .run(contexto -> assertThat(contexto.getBean(BcbClient.class)).isSameAs(proprio));
    }
}
