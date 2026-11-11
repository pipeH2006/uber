package com.uber.pagos.infrastructure.adapter.in.rest.dto;

import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.MetodoPago;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PagoRequest(
        @NotNull(message = "El viajeId es obligatorio")
        Long viajeId,

        @NotNull(message = "El usuarioId es obligatorio")
        Long usuarioId,

        @NotNull(message = "El monto es obligatorio")
        @Positive(message = "El monto debe ser positivo")
        BigDecimal monto,

        String moneda,

        @NotNull(message = "El método de pago es obligatorio")
        MetodoPago metodoPago,

        EstadoPago estado
) {
}
