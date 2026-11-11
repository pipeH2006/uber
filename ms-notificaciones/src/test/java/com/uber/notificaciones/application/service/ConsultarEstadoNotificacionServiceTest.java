package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.out.NotificacionRepositoryPort;
import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.model.Notificacion;
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
class ConsultarEstadoNotificacionServiceTest {

    @Mock
    private NotificacionRepositoryPort repository;

    private ConsultarEstadoNotificacionService service;

    @BeforeEach
    void setUp() {
        service = new ConsultarEstadoNotificacionService(repository);
    }

    @Test
    void estado_existente_devuelveNotificacion() {
        Notificacion n = Notificacion.builder().id(1L).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(n));

        assertThat(service.estado(1L)).isEqualTo(n);
    }

    @Test
    void estado_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.estado(99L))
                .isInstanceOf(NotificacionNoEncontradaException.class);
    }
}
