package com.uber.viajes.infraestructure.driver_adapters.external_repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.uber.viajes.domain.exception.ErrorExternoException;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Ruta;
import com.uber.viajes.domain.model.gateway.RutasGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;

/**
 * Adaptador del gateway de rutas usando OSRM (Open Source Routing Machine),
 * un motor de enrutamiento open source basado en OpenStreetMap.
 *
 * <p>No requiere API key: usa el servidor público de demostración de OSRM.
 */
@Slf4j
public class OsrmGatewayImpl implements RutasGateway {

    private static final String RUTA_DRIVING = "route/v1/driving";

    private final RestClient restClient;

    public OsrmGatewayImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Ruta calcularRuta(Coordenada origen, Coordenada destino) {
        // OSRM usa el orden (longitud,latitud)
        String coordenadas = origen.lng() + "," + origen.lat()
                + ";" + destino.lng() + "," + destino.lat();
        String path = RUTA_DRIVING + "/" + coordenadas + "?overview=false";

        JsonNode root = restClient.get()
                .uri(path)
                .retrieve()
                .body(JsonNode.class);

        String code = (root != null && root.hasNonNull("code")) ? root.get("code").asText() : null;
        if (!"Ok".equals(code)) {
            throw new ErrorExternoException("OSRM respondió con un estado inesperado: " + code);
        }

        double metros = root.path("routes").path(0).path("distance").asDouble();
        double segundos = root.path("routes").path(0).path("duration").asDouble();
        return new Ruta(metros / 1000.0, segundos / 60.0);
    }
}
