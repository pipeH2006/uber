package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.exception.EstadoInvalidoException;
import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.gateway.NotificacionGateway;
import com.uber.notificaciones.domain.model.gateway.TelegramGateway;
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
class ReintentarNotificacionUseCaseTest {
    @Mock
    private NotificacionGateway notificacionGateway;

    @Mock
    private TelegramGateway telegramGateway;

    private ReintentarNotificacionUseCase useCase;

    @BeforeEach
    void setUp() {
        NotificacionSender sender = new NotificacionSender(notificacionGateway, telegramGateway);
        useCase = new ReintentarNotificacionUseCase(notificacionGateway, sender);
    }

    @Test
    void reintentar_fallidaExitoso_marcaEnviada() {
        Notificacion n = Notificacion.builder()
                .id(1L).destinatario("123456").mensaje("Hola")
                .estado(EstadoNotificacion.FALLIDA).intentos(1).build();
        when(notificacionGateway.buscarPorId(1L)).thenReturn(Optional.of(n));
        when(notificacionGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Notificacion resultado = useCase.reintentar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(resultado.getIntentos()).isEqualTo(2);
    }

    @Test
    void reintentar_noFallida_lanzaEstadoInvalido() {
        Notificacion n = Notificacion.builder().id(1L).estado(EstadoNotificacion.ENVIADA).build();
        when(notificacionGateway.buscarPorId(1L)).thenReturn(Optional.of(n));

        assertThatThrownBy(() -> useCase.reintentar(1L))
                .isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void reintentar_inexistente_lanzaNotFound() {
        when(notificacionGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.reintentar(99L))
                .isInstanceOf(NotificacionNoEncontradaException.class);
    }

    @Test
    void reintentar_fallidaEnvioFallido_lanzaEnvioNotificacion() {
        Notificacion n = Notificacion.builder()
                .id(1L).destinatario("123456").mensaje("Hola")
                .estado(EstadoNotificacion.FALLIDA).intentos(1).build();
        when(notificacionGateway.buscarPorId(1L)).thenReturn(Optional.of(n));
        when(notificacionGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new EnvioNotificacionException("fallo"))
                .when(telegramGateway).enviar("123456", "Hola");

        assertThatThrownBy(() -> useCase.reintentar(1L))
                .isInstanceOf(EnvioNotificacionException.class);
    }
}
