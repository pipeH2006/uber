package com.uber.pagos.application.port.in;

import java.math.BigDecimal;

public interface ConsultarTipoCambioUseCase {

    BigDecimal tipoCambio(String moneda);
}
