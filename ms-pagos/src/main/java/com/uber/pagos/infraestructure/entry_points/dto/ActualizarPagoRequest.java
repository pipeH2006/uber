package com.uber.pagos.infraestructure.entry_points.dto;

import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.MetodoPago;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ActualizarPagoRequest(
        @Positive(message = "El monto debe ser positivo")
        BigDecimal monto,

        String moneda,

        MetodoPago metodoPago,

        EstadoPago estado
) {
}
