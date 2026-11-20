package com.uber.pagos.domain.model.gateway;

import java.math.BigDecimal;

public interface TipoCambioGateway {
    BigDecimal obtenerTipoCambio(String monedaDestino);
}
