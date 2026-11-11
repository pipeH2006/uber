package com.uber.viajes.infrastructure.adapter.out.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.uber.viajes.application.port.out.TipoCambioPort;
import com.uber.viajes.domain.exception.ErrorExternoException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Locale;

public class DolarApiAdapter implements TipoCambioPort {

    private final RestClient restClient;

    public DolarApiAdapter(RestClient restClient) {
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
