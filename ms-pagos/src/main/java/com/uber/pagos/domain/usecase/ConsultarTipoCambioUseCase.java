package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.model.gateway.TipoCambioGateway;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class ConsultarTipoCambioUseCase {

    private final TipoCambioGateway tipoCambioGateway;

    public BigDecimal tipoCambio(String moneda) {
        return tipoCambioGateway.obtenerTipoCambio(moneda);
    }
}
