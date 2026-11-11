package com.uber.viajes.application.service;

import com.uber.viajes.application.port.out.NotificacionPort;
import com.uber.viajes.application.port.out.ViajeRepositoryPort;
import com.uber.viajes.domain.exception.EstadoViajeInvalidoException;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinalizarViajeServiceTest {

    @Mock
    private ViajeRepositoryPort repository;

    @Mock
    private NotificacionPort notificacion;

    private FinalizarViajeService service;

    @BeforeEach
    void setUp() {
        service = new FinalizarViajeService(repository, notificacion);
    }

    @Test
    void finalizar_enCurso_pasaAFinalizadoYNotificaPasajero() {
        Viaje v = Viaje.builder().id(1L).pasajeroId(7L).estado(EstadoViaje.EN_CURSO)
                .costoEstimado(new BigDecimal("13.50")).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Viaje resultado = service.finalizar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoViaje.FINALIZADO);
        assertThat(resultado.getCostoFinal()).isEqualByComparingTo("13.50");
        assertThat(resultado.getFechaFin()).isNotNull();

        verify(notificacion).notificar("pasajero-7", "Tu viaje ha finalizado");
    }

    @Test
    void finalizar_estadoInvalido_lanzaExcepcion() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.ASIGNADO).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> service.finalizar(1L))
                .isInstanceOf(EstadoViajeInvalidoException.class);
    }

    @Test
    void finalizar_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.finalizar(99L))
                .isInstanceOf(ViajeNoEncontradoException.class);
    }
}
