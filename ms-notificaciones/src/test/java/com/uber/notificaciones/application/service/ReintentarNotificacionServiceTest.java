package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.out.NotificacionRepositoryPort;
import com.uber.notificaciones.application.port.out.TelegramPort;
import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.exception.EstadoInvalidoException;
import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReintentarNotificacionServiceTest {

    @Mock
    private NotificacionRepositoryPort repository;

    @Mock
    private TelegramPort telegramPort;

    private ReintentarNotificacionService service;

    @BeforeEach
    void setUp() {
        NotificacionSender sender = new NotificacionSender(repository, telegramPort);
        service = new ReintentarNotificacionService(repository, sender);
    }

    @Test
    void reintentar_fallidaExitoso_marcaEnviada() {
        Notificacion n = Notificacion.builder()
                .id(1L).destinatario("123456").mensaje("Hola")
                .estado(EstadoNotificacion.FALLIDA).intentos(1).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(n));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Notificacion resultado = service.reintentar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(resultado.getIntentos()).isEqualTo(2);
    }

    @Test
    void reintentar_noFallida_lanzaEstadoInvalido() {
        Notificacion n = Notificacion.builder().id(1L).estado(EstadoNotificacion.ENVIADA).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(n));

        assertThatThrownBy(() -> service.reintentar(1L))
                .isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void reintentar_inexistente_lanzaNotFound() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reintentar(99L))
                .isInstanceOf(NotificacionNoEncontradaException.class);
    }

    @Test
    void reintentar_fallidaEnvioFallido_lanzaEnvioNotificacion() {
        Notificacion n = Notificacion.builder()
                .id(1L).destinatario("123456").mensaje("Hola")
                .estado(EstadoNotificacion.FALLIDA).intentos(1).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(n));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new EnvioNotificacionException("fallo"))
                .when(telegramPort).enviar("123456", "Hola");

        assertThatThrownBy(() -> service.reintentar(1L))
                .isInstanceOf(EnvioNotificacionException.class);
    }
}
