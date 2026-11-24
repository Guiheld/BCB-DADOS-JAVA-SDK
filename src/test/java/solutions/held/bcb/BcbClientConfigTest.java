package solutions.held.bcb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.time.Period;
import org.junit.jupiter.api.Test;

class BcbClientConfigTest {

    @Test
    void padrao_trazValoresEsperados() {
        BcbClientConfig config = BcbClientConfig.padrao();
        assertEquals("https://api.bcb.gov.br/dados/serie", config.urlBaseSgs());
        assertEquals(3, config.maxTentativas());
        assertEquals(Period.ofYears(10), config.janelaMaximaSgs());
        assertEquals("BCB-DADOS-JAVA-SDK/0.1.0", config.userAgent());
        assertNotNull(config.objectMapper());
    }

    @Test
    void build_maxTentativasInvalido_lancaExcecao() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BcbClientConfig.builder().maxTentativas(0).build());
    }

    @Test
    void build_multiplicadorBackoffInvalido_lancaExcecao() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BcbClientConfig.builder().multiplicadorBackoff(0.5).build());
    }

    @Test
    void build_urlVazia_lancaExcecao() {
        assertThrows(
                IllegalArgumentException.class,
                () -> BcbClientConfig.builder().urlBaseSgs("  ").build());
    }

    @Test
    void build_timeoutNulo_lancaExcecao() {
        assertThrows(
                NullPointerException.class,
                () -> BcbClientConfig.builder().timeoutConexao(null).build());
    }

    @Test
    void build_personalizado_preservaValores() {
        BcbClientConfig config = BcbClientConfig.builder()
                .maxTentativas(5)
                .timeoutResposta(Duration.ofSeconds(45))
                .janelaMaximaSgs(Period.ofYears(9))
                .build();
        assertEquals(5, config.maxTentativas());
        assertEquals(Duration.ofSeconds(45), config.timeoutResposta());
        assertEquals(Period.ofYears(9), config.janelaMaximaSgs());
    }
}
