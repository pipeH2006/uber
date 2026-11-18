package com.uber.notificaciones.application.config;

import com.uber.notificaciones.infraestructure.driver_adapters.external_repository.TelegramGatewayImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
@EnableConfigurationProperties(ExternalProperties.class)
@RequiredArgsConstructor
public class RestClientConfig {

    private final ExternalProperties properties;

    @Bean
    public RestClient telegramRestClient() {
        return RestClient.builder()
                .baseUrl(properties.getTelegramBaseUrl())
                .build();
    }

    @Bean
    public TelegramGatewayImpl telegramGateway(RestClient telegramRestClient) {
        return new TelegramGatewayImpl(telegramRestClient, properties);
    }
}
