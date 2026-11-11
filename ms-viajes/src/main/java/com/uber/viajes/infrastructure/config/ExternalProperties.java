package com.uber.viajes.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "externo")
public record ExternalProperties(
        String googleMapsApiKey,
        String googleMapsBaseUrl,
        String dolarBaseUrl,
        String notificacionesUrl
) {

    public ExternalProperties {
        if (googleMapsBaseUrl == null || googleMapsBaseUrl.isBlank()) {
            googleMapsBaseUrl = "https://maps.googleapis.com";
        }
        if (dolarBaseUrl == null || dolarBaseUrl.isBlank()) {
            dolarBaseUrl = "https://open.er-api.com";
        }
        if (notificacionesUrl == null) {
            notificacionesUrl = "";
        }
    }
}
