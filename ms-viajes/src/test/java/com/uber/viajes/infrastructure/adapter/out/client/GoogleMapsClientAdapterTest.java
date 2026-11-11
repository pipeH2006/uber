package com.uber.viajes.infrastructure.adapter.out.client;

import com.uber.viajes.domain.exception.ErrorExternoException;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Ruta;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class GoogleMapsClientAdapterTest {

    private MockWebServer mockWebServer;
    private RestClient restClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        restClient = RestClient.builder().baseUrl(mockWebServer.url("/").toString()).build();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void calcularRuta_respuestaValida_convierteMetrosYSegundos() throws Exception {
        GoogleMapsClientAdapter adapter = new GoogleMapsClientAdapter(restClient, "fake-key");
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"OK\",\"rows\":[{\"elements\":[{\"distance\":{\"value\":12500},\"duration\":{\"value\":900}}]}]}"));

        Ruta ruta = adapter.calcularRuta(new Coordenada(4.71, -74.07), new Coordenada(4.60, -74.08));

        assertThat(ruta.distanciaKm()).isEqualTo(12.5);
        assertThat(ruta.duracionMin()).isEqualTo(15.0);

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).startsWith("/maps/api/distancematrix/json");
        assertThat(request.getRequestUrl().queryParameter("origins")).isEqualTo("4.71,-74.07");
        assertThat(request.getRequestUrl().queryParameter("destinations")).isEqualTo("4.6,-74.08");
        assertThat(request.getRequestUrl().queryParameter("key")).isEqualTo("fake-key");
        assertThat(request.getRequestUrl().queryParameter("units")).isEqualTo("metric");
    }

    @Test
    void calcularRuta_estadoNoOk_lanzaErrorExterno() {
        GoogleMapsClientAdapter adapter = new GoogleMapsClientAdapter(restClient, "fake-key");
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"status\":\"ZERO_RESULTS\",\"rows\":[]}"));

        assertThatThrownBy(() -> adapter.calcularRuta(new Coordenada(4.71, -74.07), new Coordenada(4.60, -74.08)))
                .isInstanceOf(ErrorExternoException.class);
    }

    @Test
    void calcularRuta_apiKeyVacia_devuelveRutaSimulada() {
        GoogleMapsClientAdapter adapter = new GoogleMapsClientAdapter(restClient, "");

        Ruta ruta = adapter.calcularRuta(new Coordenada(4.71, -74.07), new Coordenada(4.60, -74.08));

        assertThat(ruta).isEqualTo(new Ruta(5.0, 10.0));
        assertThat(mockWebServer.getRequestCount()).isZero();
    }

    @Test
    void calcularRuta_apiKeyNula_devuelveRutaSimulada() {
        GoogleMapsClientAdapter adapter = new GoogleMapsClientAdapter(restClient, null);

        Ruta ruta = adapter.calcularRuta(new Coordenada(4.71, -74.07), new Coordenada(4.60, -74.08));

        assertThat(ruta).isEqualTo(new Ruta(5.0, 10.0));
    }
}
