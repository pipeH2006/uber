package com.uber.pagos.application.port.in;

import com.uber.pagos.domain.model.MetodoPago;
import com.uber.pagos.domain.model.Pago;

import java.math.BigDecimal;

public interface ProcesarPagoUseCase {

    Pago procesar(Long viajeId, Long usuarioId, BigDecimal monto, String moneda, MetodoPago metodoPago);
}
