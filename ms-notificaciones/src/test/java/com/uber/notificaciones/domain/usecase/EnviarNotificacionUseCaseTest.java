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
class EnviarNotificacionUseCaseTest {

    @Mock
    private NotificacionGateway notificacionGateway;

    @Mock
    private TelegramGateway telegramGateway;

    private EnviarNotificacionUseCase useCase;

    @BeforeEach
    void setUp() {
        NotificacionSender sender = new NotificacionSender(notificacionGateway, telegramGateway);
        useCase = new EnviarNotificacionUseCase(notificacionGateway, sender);
    }

    @Test
    void enviar_exitoso_marcaEnviadaEIncrementaIntentos() {
        when(notificacionGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Notificacion resultado = useCase.enviar("123456", "Hola", TipoNotificacion.VIAJE_SOLICITADO);

        assertThat(resultado.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(resultado.getIntentos()).isEqualTo(1);
        assertThat(resultado.getFechaEnvio()).isNotNull();
        verify(telegramGateway).enviar("123456", "Hola");
    }

    @Test
    void enviar_fallido_marcaFallidaYLanzaExcepcion() {
        when(notificacionGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new EnvioNotificacionException("fallo"))
                .when(telegramGateway).enviar("123456", "Hola");

        assertThatThrownBy(() -> useCase.enviar("123456", "Hola", TipoNotificacion.GENERICO))
                .isInstanceOf(EnvioNotificacionException.class);

        ArgumentCaptor<Notificacion> captor = ArgumentCaptor.forClass(Notificacion.class);
        verify(notificacionGateway, times(2)).guardar(captor.capture());
        assertThat(captor.getAllValues().get(1).getEstado()).isEqualTo(EstadoNotificacion.FALLIDA);
        assertThat(captor.getAllValues().get(1).getIntentos()).isEqualTo(1);
    }
}
