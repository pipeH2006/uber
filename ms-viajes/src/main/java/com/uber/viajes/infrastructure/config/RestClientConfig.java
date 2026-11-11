package com.uber.viajes.infrastructure.config;

import com.uber.viajes.infrastructure.adapter.out.client.DolarApiAdapter;
import com.uber.viajes.infrastructure.adapter.out.client.GoogleMapsClientAdapter;
import com.uber.viajes.infrastructure.adapter.out.client.NotificacionRestAdapter;
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
    public GoogleMapsClientAdapter googleMapsClientAdapter(RestClient googleMapsRestClient, ExternalProperties props) {
        return new GoogleMapsClientAdapter(googleMapsRestClient, props.googleMapsApiKey());
    }

    @Bean
    public DolarApiAdapter dolarApiAdapter(RestClient dolarRestClient) {
        return new DolarApiAdapter(dolarRestClient);
    }

    @Bean
    public NotificacionRestAdapter notificacionRestAdapter(RestClient notificacionesRestClient, ExternalProperties props) {
        return new NotificacionRestAdapter(notificacionesRestClient, props.notificacionesUrl());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECT_TIMEOUT);
        factory.setReadTimeout(READ_TIMEOUT);
        return factory;
    }
}
