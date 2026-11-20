package com.uber.notificaciones.application.config;

import com.uber.notificaciones.domain.model.gateway.RedactorGateway;
import com.uber.notificaciones.domain.model.gateway.UsuarioGateway;
import com.uber.notificaciones.infraestructure.driver_adapters.external_repository.GroqRedactorGatewayImpl;
import com.uber.notificaciones.infraestructure.driver_adapters.external_repository.TelegramGatewayImpl;
import com.uber.notificaciones.infraestructure.driver_adapters.external_repository.UsuarioGatewayImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

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

    @Bean
    public RestClient groqRestClient() {
        return RestClient.builder()
                .baseUrl(properties.getGroqBaseUrl())
                .requestFactory(requestFactory(Duration.ofSeconds(8)))
                .build();
    }

    @Bean
    public RedactorGateway redactorGateway(RestClient groqRestClient) {
        return new GroqRedactorGatewayImpl(groqRestClient, properties.getGroqApiKey());
    }

    @Bean
    public RestClient usuariosRestClient() {
        String base = properties.getUsuariosUrl().isBlank() ? "http://localhost:8080" : properties.getUsuariosUrl();
        return RestClient.builder()
                .baseUrl(base)
                .requestFactory(requestFactory(Duration.ofSeconds(10)))
                .build();
    }

    @Bean
    public UsuarioGateway usuarioGateway(RestClient usuariosRestClient) {
        return new UsuarioGatewayImpl(usuariosRestClient, properties.getUsuariosUrl());
    }

    private ClientHttpRequestFactory requestFactory(Duration readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(2));
        factory.setReadTimeout(readTimeout);
        return factory;
    }
}
