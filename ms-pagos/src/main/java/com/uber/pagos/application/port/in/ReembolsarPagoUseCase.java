package com.uber.pagos.application.port.in;

import com.uber.pagos.domain.model.Pago;

public interface ReembolsarPagoUseCase {

    Pago reembolsar(Long id);
}
