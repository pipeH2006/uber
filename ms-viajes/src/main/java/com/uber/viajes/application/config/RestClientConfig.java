package com.uber.viajes.application.config;

import com.uber.viajes.domain.model.gateway.MapsGateway;
import com.uber.viajes.domain.model.gateway.NotificacionGateway;
import com.uber.viajes.domain.model.gateway.TipoCambioGateway;
import com.uber.viajes.infraestructure.driver_adapters.external_repository.DolarApiGatewayImpl;
import com.uber.viajes.infraestructure.driver_adapters.external_repository.GoogleMapsGatewayImpl;
import com.uber.viajes.infraestructure.driver_adapters.external_repository.NotificacionRestGatewayImpl;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@EnableConfigurationProperties(ExternalProperties.class)
public class RestClientConfig {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(2);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(5);

    @Bean
    public RestClient googleMapsRestClient(ExternalProperties props) {
        return RestClient.builder()
                .baseUrl(props.googleMapsBaseUrl())
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    @Bean
    public RestClient dolarRestClient(ExternalProperties props) {
        return RestClient.builder()
                .baseUrl(props.dolarBaseUrl())
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    @Bean
    public RestClient notificacionesRestClient(ExternalProperties props) {
        String base = (props.notificacionesUrl() == null || props.notificacionesUrl().isBlank())
                ? "http://localhost:8080" : props.notificacionesUrl();
        return RestClient.builder()
                .baseUrl(base)
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    @Bean
    public MapsGateway mapsGateway(RestClient googleMapsRestClient, ExternalProperties props) {
        return new GoogleMapsGatewayImpl(googleMapsRestClient, props.googleMapsApiKey());
    }

    @Bean
    public TipoCambioGateway tipoCambioGateway(RestClient dolarRestClient) {
        return new DolarApiGatewayImpl(dolarRestClient);
    }

    @Bean
    public NotificacionGateway notificacionGateway(RestClient notificacionesRestClient, ExternalProperties props) {
        return new NotificacionRestGatewayImpl(notificacionesRestClient, props.notificacionesUrl());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        return factory;
    }
}
