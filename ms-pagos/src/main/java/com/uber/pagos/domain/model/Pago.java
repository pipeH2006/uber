package com.uber.pagos.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Agregado raíz del subdominio de pagos. Representa un pago asociado a un viaje
 * y a un usuario, junto con su estado y los datos de facturación/fraude.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {

    private Long id;
    private Long viajeId;
    private Long usuarioId;
    private BigDecimal monto;
    private String moneda;
    private MetodoPago metodoPago;
    private EstadoPago estado;
    private LocalDateTime fecha;
    private String referencia;
    private String riesgoFraude;
}
