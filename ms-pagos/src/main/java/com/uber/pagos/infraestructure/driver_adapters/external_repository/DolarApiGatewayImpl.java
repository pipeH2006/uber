package com.uber.pagos.infraestructure.driver_adapters.external_repository;

import com.uber.pagos.domain.exception.ErrorExternoException;
import com.uber.pagos.domain.model.gateway.TipoCambioGateway;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DolarApiGatewayImpl implements TipoCambioGateway {
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
        try {
            DolarApiResponse response = restClient.get()
                    .uri("/v6/latest/USD")
                    .retrieve()
                    .body(DolarApiResponse.class);
            if (response == null || response.rates() == null) {
                throw new ErrorExternoException("Respuesta inválida del proveedor de tipo de cambio");
            }
            BigDecimal rate = response.rates().get(moneda);
            if (rate == null) {
                throw new ErrorExternoException("No se encontró tipo de cambio para la moneda: " + moneda);
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

    private record TasaEnCache(BigDecimal tasa, Instant expiraEn) {
    }
}
