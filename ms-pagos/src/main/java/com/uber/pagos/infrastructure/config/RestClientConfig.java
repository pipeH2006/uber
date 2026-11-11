package com.uber.pagos.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.pagos.infrastructure.adapter.out.client.DolarApiAdapter;
import com.uber.pagos.infrastructure.adapter.out.client.NotificacionRestAdapter;
import com.uber.pagos.infrastructure.adapter.out.client.OpenAiFraudeAdapter;
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

    @Bean
    public RestClient dolarApiRestClient(ExternalProperties props) {
        return RestClient.builder()
                .baseUrl(props.getDolarBaseUrl())
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    @Bean
    public RestClient openAiRestClient(ExternalProperties props) {
        return RestClient.builder()
                .baseUrl(props.getOpenAiBaseUrl())
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    @Bean
    public RestClient notificacionRestClient(ExternalProperties props) {
        String baseUrl = (props.getNotificacionesUrl() == null || props.getNotificacionesUrl().isBlank())
                ? "http://localhost" : props.getNotificacionesUrl();
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    @Bean
    public DolarApiAdapter dolarApiAdapter(RestClient dolarApiRestClient) {
        return new DolarApiAdapter(dolarApiRestClient);
    }

    @Bean
    public OpenAiFraudeAdapter openAiFraudeAdapter(RestClient openAiRestClient,
                                                   ExternalProperties props,
                                                   ObjectMapper objectMapper) {
        return new OpenAiFraudeAdapter(openAiRestClient, props.getOpenAiApiKey(), objectMapper);
    }

    @Bean
    public NotificacionRestAdapter notificacionRestAdapter(RestClient notificacionRestClient,
                                                           ExternalProperties props) {
        return new NotificacionRestAdapter(notificacionRestClient, props.getNotificacionesUrl());
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(10));
        return factory;
    }
}
