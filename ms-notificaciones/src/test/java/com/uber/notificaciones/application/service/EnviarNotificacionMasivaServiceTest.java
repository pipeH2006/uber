package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.out.NotificacionRepositoryPort;
import com.uber.notificaciones.application.port.out.TelegramPort;
import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;
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
class EnviarNotificacionMasivaServiceTest {

    @Mock
    private NotificacionRepositoryPort repository;

    @Mock
    private TelegramPort telegramPort;

    private EnviarNotificacionMasivaService service;

    @BeforeEach
    void setUp() {
        NotificacionSender sender = new NotificacionSender(repository, telegramPort);
        service = new EnviarNotificacionMasivaService(repository, sender);
    }

    @Test
    void enviarMasivo_todosExitosos_devuelveEnviadas() {
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        List<Notificacion> resultados = service.enviarMasivo(
                List.of("111", "222"), "Hola", TipoNotificacion.GENERICO);

        assertThat(resultados).hasSize(2);
        assertThat(resultados).allMatch(n -> n.getEstado() == EstadoNotificacion.ENVIADA);
    }

    @Test
    void enviarMasivo_falloParcial_continuaConElResto() {
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new EnvioNotificacionException("fallo"))
                .when(telegramPort).enviar("111", "Hola");

        List<Notificacion> resultados = service.enviarMasivo(
                List.of("111", "222", "333"), "Hola", TipoNotificacion.GENERICO);

        assertThat(resultados).hasSize(3);
        assertThat(resultados.get(0).getEstado()).isEqualTo(EstadoNotificacion.FALLIDA);
        assertThat(resultados.get(1).getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(resultados.get(2).getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
    }
}
