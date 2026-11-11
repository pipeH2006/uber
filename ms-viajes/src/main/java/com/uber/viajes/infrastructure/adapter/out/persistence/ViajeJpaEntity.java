package com.uber.viajes.infrastructure.adapter.out.persistence;

import com.uber.viajes.domain.model.EstadoViaje;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "viajes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ViajeJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pasajero_id")
    private Long pasajeroId;

    @Column(name = "conductor_id")
    private Long conductorId;

    @Column(name = "origen_lat")
    private double origenLat;

    @Column(name = "origen_lng")
    private double origenLng;

    @Column(name = "origen_direccion")
    private String origenDireccion;

    @Column(name = "destino_lat")
    private double destinoLat;

    @Column(name = "destino_lng")
    private double destinoLng;

    @Column(name = "destino_direccion")
    private String destinoDireccion;

    @Column(name = "distancia_km")
    private double distanciaKm;

    @Column(name = "duracion_min")
    private double duracionMin;

    @Column(name = "costo_estimado", precision = 10, scale = 2)
    private BigDecimal costoEstimado;

    @Column(name = "costo_final", precision = 10, scale = 2)
    private BigDecimal costoFinal;

    private String moneda;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoViaje estado;

    @Column(name = "fecha_solicitud")
    private LocalDateTime fechaSolicitud;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin")
    private LocalDateTime fechaFin;
}
