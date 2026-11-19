package com.uber.viajes.application.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "externo")
public record ExternalProperties(
        String osrmBaseUrl,
        String dolarBaseUrl,
        String notificacionesUrl
) {

    public ExternalProperties {
        if (osrmBaseUrl == null || osrmBaseUrl.isBlank()) {
            osrmBaseUrl = "https://router.project-osrm.org";
        }
        if (dolarBaseUrl == null || dolarBaseUrl.isBlank()) {
            dolarBaseUrl = "https://open.er-api.com";
        }
        if (notificacionesUrl == null) {
            notificacionesUrl = "";
        }
    }
}
