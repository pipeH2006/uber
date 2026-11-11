package com.uber.pagos.application.port.out;

import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResultadoFraude;

public interface FraudePort {

    ResultadoFraude evaluar(Pago pago);
}
