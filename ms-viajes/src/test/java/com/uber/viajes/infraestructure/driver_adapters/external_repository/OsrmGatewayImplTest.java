package com.uber.viajes.infraestructure.driver_adapters.external_repository;

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

class OsrmGatewayImplTest {
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
        OsrmGatewayImpl gateway = new OsrmGatewayImpl(restClient);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"code\":\"Ok\",\"routes\":[{\"distance\":12500.0,\"duration\":900.0,"
                        + "\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[-74.07,4.71],[-74.075,4.65],[-74.08,4.6]]}}]}"));

        Ruta ruta = gateway.calcularRuta(new Coordenada(4.71, -74.07), new Coordenada(4.60, -74.08));

        assertThat(ruta.distanciaKm()).isEqualTo(12.5);
        assertThat(ruta.duracionMin()).isEqualTo(15.0);
        assertThat(ruta.puntos()).containsExactly(
                new Coordenada(4.71, -74.07), new Coordenada(4.65, -74.075), new Coordenada(4.6, -74.08));

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath()).startsWith("/route/v1/driving/");
        assertThat(request.getPath()).contains("-74.07,4.71;-74.08,4.6");
        assertThat(request.getPath()).contains("overview=full").contains("geometries=geojson");
    }

    @Test
    void calcularRuta_codigoNoOk_lanzaErrorExterno() {
        OsrmGatewayImpl gateway = new OsrmGatewayImpl(restClient);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"code\":\"NoRoute\",\"routes\":[]}"));

        assertThatThrownBy(() -> gateway.calcularRuta(new Coordenada(4.71, -74.07), new Coordenada(4.60, -74.08)))
                .isInstanceOf(ErrorExternoException.class);
    }

    @Test
    void calcularRuta_sinGeometria_devuelveTrazadoVacio() {
        OsrmGatewayImpl gateway = new OsrmGatewayImpl(restClient);
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{\"code\":\"Ok\",\"routes\":[{\"distance\":1000.0,\"duration\":60.0}]}"));

        Ruta ruta = gateway.calcularRuta(new Coordenada(4.71, -74.07), new Coordenada(4.60, -74.08));

        assertThat(ruta.puntos()).isEmpty();
    }
}
