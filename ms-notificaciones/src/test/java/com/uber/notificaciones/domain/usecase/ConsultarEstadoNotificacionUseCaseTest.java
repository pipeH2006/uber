package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.gateway.NotificacionGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarEstadoNotificacionUseCaseTest {
    @Mock
    private NotificacionGateway notificacionGateway;

    private ConsultarEstadoNotificacionUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarEstadoNotificacionUseCase(notificacionGateway);
    }

    @Test
    void estado_existente_devuelveNotificacion() {
        Notificacion n = Notificacion.builder().id(1L).build();
        when(notificacionGateway.buscarPorId(1L)).thenReturn(Optional.of(n));

        assertThat(useCase.estado(1L)).isEqualTo(n);
    }

    @Test
    void estado_inexistente_lanzaExcepcion() {
        when(notificacionGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.estado(99L))
                .isInstanceOf(NotificacionNoEncontradaException.class);
    }
}
