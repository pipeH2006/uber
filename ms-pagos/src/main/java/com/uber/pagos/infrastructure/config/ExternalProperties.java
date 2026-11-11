package com.uber.pagos.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades externas de integración del microservicio de pagos.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "externo")
public class ExternalProperties {

    private String dolarBaseUrl = "https://open.er-api.com";
    private String openAiApiKey = "";
    private String openAiBaseUrl = "https://api.openai.com";
    private String notificacionesUrl = "";
}
