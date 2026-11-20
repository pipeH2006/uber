package com.uber.notificaciones.application.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración de los servicios externos (Telegram).
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "externo")
public class ExternalProperties {

    private String telegramBotToken = "";

    private String telegramBaseUrl = "https://api.telegram.org";

    private String groqApiKey = "";

    private String groqBaseUrl = "https://api.groq.com/openai";

    private String usuariosUrl = "";
}
