package com.uber.viajes.application.service;

import com.uber.viajes.application.port.in.Seguimiento;
import com.uber.viajes.application.port.out.MapsClientPort;
import com.uber.viajes.application.port.out.ViajeRepositoryPort;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Ruta;
import com.uber.viajes.domain.model.Viaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeguimientoViajeServiceTest {

    @Mock
    private ViajeRepositoryPort repository;

    @Mock
    private MapsClientPort mapsClient;

    private SeguimientoViajeService service;

    @BeforeEach
    void setUp() {
        service = new SeguimientoViajeService(repository, mapsClient);
    }

    @Test
    void seguir_recalculaRutaYDevuelveSeguimiento() {
        Viaje v = Viaje.builder()
                .id(1L)
                .origenLat(4.71).origenLng(-74.07)
                .destinoLat(4.60).destinoLng(-74.08)
                .costoEstimado(new BigDecimal("13.50"))
                .estado(EstadoViaje.EN_CURSO)
                .build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(mapsClient.calcularRuta(new Coordenada(4.71, -74.07), new Coordenada(4.60, -74.08)))
                .thenReturn(new Ruta(12.5, 15.0));

        Seguimiento seguimiento = service.seguir(1L);

        assertThat(seguimiento.viajeId()).isEqualTo(1L);
        assertThat(seguimiento.estado()).isEqualTo(EstadoViaje.EN_CURSO);
        assertThat(seguimiento.distanciaKm()).isEqualTo(12.5);
        assertThat(seguimiento.duracionMin()).isEqualTo(15.0);
        assertThat(seguimiento.costoEstimado()).isEqualByComparingTo("13.50");
    }

    @Test
    void seguir_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.seguir(99L))
                .isInstanceOf(ViajeNoEncontradoException.class);
    }
}
