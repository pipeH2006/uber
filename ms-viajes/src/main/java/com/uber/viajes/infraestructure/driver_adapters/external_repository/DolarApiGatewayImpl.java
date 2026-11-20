package com.uber.viajes.infraestructure.driver_adapters.external_repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.uber.viajes.domain.exception.ErrorExternoException;
import com.uber.viajes.domain.model.gateway.TipoCambioGateway;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DolarApiGatewayImpl implements TipoCambioGateway {

    /** El tipo de cambio del proveedor se actualiza una vez al día: una hora de caché es segura. */
    static final Duration TTL = Duration.ofHours(1);

    private final Map<String, TasaEnCache> cache = new ConcurrentHashMap<>();
    private final Clock clock;

    private final RestClient restClient;

    public DolarApiGatewayImpl(RestClient restClient) {
        this(restClient, Clock.systemUTC());
    }

    DolarApiGatewayImpl(RestClient restClient, Clock clock) {
        this.restClient = restClient;
        this.clock = clock;
    }

    @Override
    public BigDecimal obtenerTipoCambio(String monedaDestino) {
        String moneda = monedaDestino.toUpperCase(Locale.ROOT);
        BigDecimal enCache = tasaVigente(moneda);
        if (enCache != null) {
            return enCache;
        }
        synchronized (this) {
            enCache = tasaVigente(moneda);
            if (enCache != null) {
                return enCache;
            }
            BigDecimal tasa = consultarProveedor(moneda);
            cache.put(moneda, new TasaEnCache(tasa, clock.instant().plus(TTL)));
            return tasa;
        }
    }

    private BigDecimal tasaVigente(String moneda) {
        TasaEnCache cacheada = cache.get(moneda);
        return (cacheada != null && cacheada.expiraEn().isAfter(clock.instant())) ? cacheada.tasa() : null;
    }

    private BigDecimal consultarProveedor(String moneda) {
        JsonNode root = restClient.get()
                .uri("v6/latest/USD")
                .retrieve()
                .body(JsonNode.class);

        JsonNode rate = (root != null) ? root.path("rates").path(moneda) : null;
        if (rate == null || rate.isMissingNode() || rate.isNull()) {
            throw new ErrorExternoException("No se encontró la tasa de cambio para " + moneda);
        }
        return rate.decimalValue();
    }

    private record TasaEnCache(BigDecimal tasa, Instant expiraEn) {
    }
}
