package com.uber.notificaciones.infrastructure.config;

import com.uber.notificaciones.infrastructure.adapter.out.client.TelegramRestAdapter;
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
    public TelegramRestAdapter telegramRestAdapter(RestClient telegramRestClient) {
        return new TelegramRestAdapter(telegramRestClient, properties);
    }
}
