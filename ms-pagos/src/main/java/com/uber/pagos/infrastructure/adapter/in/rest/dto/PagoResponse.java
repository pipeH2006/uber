package com.uber.pagos.infrastructure.adapter.in.rest.dto;

import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.MetodoPago;
import com.uber.pagos.domain.model.Pago;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Long id,
        Long viajeId,
        Long usuarioId,
        BigDecimal monto,
        String moneda,
        MetodoPago metodoPago,
        EstadoPago estado,
        LocalDateTime fecha,
        String referencia,
        String riesgoFraude
) {

    public static PagoResponse from(Pago p) {
        return new PagoResponse(
                p.getId(),
                p.getViajeId(),
                p.getUsuarioId(),
                p.getMonto(),
                p.getMoneda(),
                p.getMetodoPago(),
                p.getEstado(),
                p.getFecha(),
                p.getReferencia(),
                p.getRiesgoFraude()
        );
    }
}
