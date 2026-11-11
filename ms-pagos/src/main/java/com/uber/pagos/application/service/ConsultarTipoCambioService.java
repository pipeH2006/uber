package com.uber.pagos.application.service;

import com.uber.pagos.application.port.in.ConsultarTipoCambioUseCase;
import com.uber.pagos.application.port.out.TipoCambioPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ConsultarTipoCambioService implements ConsultarTipoCambioUseCase {

    private final TipoCambioPort tipoCambioPort;

    @Override
    public BigDecimal tipoCambio(String moneda) {
        return tipoCambioPort.obtenerTipoCambio(moneda);
    }
}
