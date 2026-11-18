package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Ruta;
import com.uber.viajes.domain.model.Seguimiento;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.MapsGateway;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
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
class SeguimientoViajeUseCaseTest {

    @Mock
    private ViajeGateway viajeGateway;

    @Mock
    private MapsGateway mapsGateway;

    private SeguimientoViajeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SeguimientoViajeUseCase(viajeGateway, mapsGateway);
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
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(mapsGateway.calcularRuta(new Coordenada(4.71, -74.07), new Coordenada(4.60, -74.08)))
                .thenReturn(new Ruta(12.5, 15.0));

        Seguimiento seguimiento = useCase.seguir(1L);

        assertThat(seguimiento.viajeId()).isEqualTo(1L);
        assertThat(seguimiento.estado()).isEqualTo(EstadoViaje.EN_CURSO);
        assertThat(seguimiento.distanciaKm()).isEqualTo(12.5);
        assertThat(seguimiento.duracionMin()).isEqualTo(15.0);
        assertThat(seguimiento.costoEstimado()).isEqualByComparingTo("13.50");
    }

    @Test
    void seguir_inexistente_lanzaExcepcion() {
        when(viajeGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.seguir(99L))
                .isInstanceOf(ViajeNoEncontradoException.class);
    }
}
