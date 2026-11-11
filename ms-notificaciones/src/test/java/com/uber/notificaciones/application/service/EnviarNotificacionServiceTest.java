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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarNotificacionServiceTest {

    @Mock
    private NotificacionRepositoryPort repository;

    @Mock
    private TelegramPort telegramPort;

    private EnviarNotificacionService service;

    @BeforeEach
    void setUp() {
        NotificacionSender sender = new NotificacionSender(repository, telegramPort);
        service = new EnviarNotificacionService(repository, sender);
    }

    @Test
    void enviar_exitoso_marcaEnviadaEIncrementaIntentos() {
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Notificacion resultado = service.enviar("123456", "Hola", TipoNotificacion.VIAJE_SOLICITADO);

        assertThat(resultado.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(resultado.getIntentos()).isEqualTo(1);
        assertThat(resultado.getFechaEnvio()).isNotNull();
        verify(telegramPort).enviar("123456", "Hola");
    }

    @Test
    void enviar_fallido_marcaFallidaYLanzaExcepcion() {
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new EnvioNotificacionException("fallo"))
                .when(telegramPort).enviar("123456", "Hola");

        assertThatThrownBy(() -> service.enviar("123456", "Hola", TipoNotificacion.GENERICO))
                .isInstanceOf(EnvioNotificacionException.class);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(repository, times(2)).guardar(captor.capture());
        assertThat(captor.getAllValues().get(1).getEstado()).isEqualTo(EstadoNotificacion.FALLIDA);
        assertThat(captor.getAllValues().get(1).getIntentos()).isEqualTo(1);
    }
}
