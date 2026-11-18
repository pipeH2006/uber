package com.uber.pagos.domain.model.gateway;

import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResultadoFraude;

public interface FraudeGateway {

    ResultadoFraude evaluar(Pago pago);
}
