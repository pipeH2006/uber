package com.uber.viajes.infrastructure.adapter.out.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.uber.viajes.application.port.out.MapsClientPort;
import com.uber.viajes.domain.exception.ErrorExternoException;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Ruta;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

@Slf4j
public class GoogleMapsClientAdapter implements MapsClientPort {

    private static final String RUTA_DISTANCE_MATRIX = "maps/api/distancematrix/json";

    private final RestClient restClient;
    private final String apiKey;

    public GoogleMapsClientAdapter(RestClient restClient, String apiKey) {
        this.restClient = restClient;
        this.apiKey = apiKey;
    }

    @Override
    public Ruta calcularRuta(Coordenada origen, Coordenada destino) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("Google Maps API key no configurada; se devuelve una ruta simulada");
            return new Ruta(5.0, 10.0);
        }

        String origins = origen.lat() + "," + origen.lng();
        String destinations = destino.lat() + "," + destino.lng();
        String path = RUTA_DISTANCE_MATRIX + "?origins=" + origins
                + "&destinations=" + destinations
                + "&key=" + apiKey
                + "&units=metric";

        JsonNode root = restClient.get()
                .uri(path)
                .retrieve()
                .body(JsonNode.class);

        String status = (root != null && root.hasNonNull("status")) ? root.get("status").asText() : null;
        if (!"OK".equals(status)) {
            throw new ErrorExternoException("Google Maps respondió con un estado inesperado: " + status);
        }

        double metros = root.path("rows").path(0).path("elements").path(0)
                .path("distance").path("value").asDouble();
        double segundos = root.path("rows").path(0).path("elements").path(0)
                .path("duration").path("value").asDouble();
        return new Ruta(metros / 1000.0, segundos / 60.0);
    }
}
