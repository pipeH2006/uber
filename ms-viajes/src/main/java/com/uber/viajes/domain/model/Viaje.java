package com.uber.viajes.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Agregado raíz del subdominio de viajes.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Viaje {

    private Long id;
    private Long pasajeroId;
    private Long conductorId;
    private double origenLat;
    private double origenLng;
    private String origenDireccion;
    private double destinoLat;
    private double destinoLng;
    private String destinoDireccion;
    private double distanciaKm;
    private double duracionMin;
    private BigDecimal costoEstimado;
    private BigDecimal costoFinal;
    @Builder.Default
    private String moneda = "USD";
    private EstadoViaje estado;
    private LocalDateTime fechaSolicitud;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
}
