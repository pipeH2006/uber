package com.uber.viajes.application.port.out;

import java.math.BigDecimal;

public interface TipoCambioPort {

    BigDecimal obtenerTipoCambio(String monedaDestino);
}
