package com.uber.pagos.application.port.out;

import java.math.BigDecimal;

public interface TipoCambioPort {

    BigDecimal obtenerTipoCambio(String monedaDestino);
}
