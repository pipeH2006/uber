package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import com.uber.notificaciones.domain.model.gateway.NotificacionGateway;
import com.uber.notificaciones.domain.model.gateway.TelegramGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarNotificacionMasivaUseCaseTest {

    @Mock
    private NotificacionGateway notificacionGateway;

    @Mock
    private TelegramGateway telegramGateway;

    private EnviarNotificacionMasivaUseCase useCase;

    @BeforeEach
    void setUp() {
        NotificacionSender sender = new NotificacionSender(notificacionGateway, telegramGateway);
        useCase = new EnviarNotificacionMasivaUseCase(notificacionGateway, sender);
    }

    @Test
    void enviarMasivo_todosExitosos_devuelveEnviadas() {
        when(notificacionGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        List<Notificacion> resultados = useCase.enviarMasivo(
                List.of("111", "222"), "Hola", TipoNotificacion.GENERICO);

        assertThat(resultados).hasSize(2);
        assertThat(resultados).allMatch(n -> n.getEstado() == EstadoNotificacion.ENVIADA);
    }

    @Test
    void enviarMasivo_falloParcial_continuaConElResto() {
        when(notificacionGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new EnvioNotificacionException("fallo"))
                .when(telegramGateway).enviar("111", "Hola");

        List<Notificacion> resultados = useCase.enviarMasivo(
                List.of("111", "222", "333"), "Hola", TipoNotificacion.GENERICO);

        assertThat(resultados).hasSize(3);
        assertThat(resultados.get(0).getEstado()).isEqualTo(EstadoNotificacion.FALLIDA);
        assertThat(resultados.get(1).getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(resultados.get(2).getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
    }
}
