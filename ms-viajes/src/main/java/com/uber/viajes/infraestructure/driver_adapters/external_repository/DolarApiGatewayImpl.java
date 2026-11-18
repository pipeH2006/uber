package com.uber.viajes.infraestructure.driver_adapters.external_repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.uber.viajes.domain.exception.ErrorExternoException;
import com.uber.viajes.domain.model.gateway.TipoCambioGateway;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Locale;

public class DolarApiGatewayImpl implements TipoCambioGateway {

    private final RestClient restClient;

    public DolarApiGatewayImpl(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public BigDecimal obtenerTipoCambio(String monedaDestino) {
        JsonNode root = restClient.get()
                .uri("v6/latest/USD")
                .retrieve()
                .body(JsonNode.class);

        String moneda = monedaDestino.toUpperCase(Locale.ROOT);
        JsonNode rate = (root != null) ? root.path("rates").path(moneda) : null;
        if (rate == null || rate.isMissingNode() || rate.isNull()) {
            throw new ErrorExternoException("No se encontró la tasa de cambio para " + moneda);
        }
        return rate.decimalValue();
    }
}
