package com.uber.pagos.infrastructure.adapter.out.client;

import com.uber.pagos.application.port.out.TipoCambioPort;
import com.uber.pagos.domain.exception.ErrorExternoException;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

public class DolarApiAdapter implements TipoCambioPort {

    private final RestClient restClient;

    public DolarApiAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public BigDecimal obtenerTipoCambio(String monedaDestino) {
        try {
            DolarApiResponse response = restClient.get()
                    .uri("/v6/latest/USD")
                    .retrieve()
                    .body(DolarApiResponse.class);
            if (response == null || response.rates() == null) {
                throw new ErrorExternoException("Respuesta inválida del proveedor de tipo de cambio");
            }
            BigDecimal rate = response.rates().get(monedaDestino.toUpperCase(Locale.ROOT));
            if (rate == null) {
                throw new ErrorExternoException("No se encontró tipo de cambio para la moneda: " + monedaDestino);
            }
            return rate;
        } catch (ErrorExternoException e) {
            throw e;
        } catch (Exception e) {
            throw new ErrorExternoException("Error al consultar tipo de cambio: " + e.getMessage(), e);
        }
    }

    private record DolarApiResponse(Map<String, BigDecimal> rates) {
    }
}
