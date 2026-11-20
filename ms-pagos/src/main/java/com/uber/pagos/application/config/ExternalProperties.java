package com.uber.pagos.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "externo")
public record ExternalProperties(
        String dolarBaseUrl,
        String groqApiKey,
        String groqBaseUrl,
        String notificacionesUrl,
        String usuariosUrl
) {

    public ExternalProperties {
        if (dolarBaseUrl == null || dolarBaseUrl.isBlank()) {
            dolarBaseUrl = "https://open.er-api.com";
        }
        if (groqApiKey == null) {
            groqApiKey = "";
        }
        if (groqBaseUrl == null || groqBaseUrl.isBlank()) {
            groqBaseUrl = "https://api.groq.com/openai";
        }
        if (notificacionesUrl == null) {
            notificacionesUrl = "";
        }
        if (usuariosUrl == null) {
            usuariosUrl = "";
        }
    }
}
