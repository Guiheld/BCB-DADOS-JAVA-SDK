package solutions.held.bcb.spring;

import solutions.held.bcb.BcbClientConfig;

/**
 * Ponto de extensão para ajustar programaticamente a configuração da SDK antes de o
 * {@link solutions.held.bcb.BcbClient} ser criado.
 *
 * <p>
 *     Declare um ou mais beans deste tipo no contexto Spring para, por exemplo, fornecer um
 *     {@link java.net.http.HttpClient} instrumentado (observabilidade, proxy), um
 *     {@link com.fasterxml.jackson.databind.ObjectMapper} próprio ou qualquer outro parâmetro não
 *     coberto por {@link BcbProperties}. Os customizadores são aplicados depois das propriedades
 *     {@code bcb.*}, de modo que têm a palavra final.
 * </p>
 *
 * @author Guilherme A. Held
 * @since 25/11/2026
 */
@FunctionalInterface
public interface BcbClientConfigCustomizer {

    /**
     * Ajusta o construtor da configuração da SDK.
     *
     * @param builder o construtor a ser ajustado
     * @since 25/11/2026
     */
    void customize(BcbClientConfig.Builder builder);
}
