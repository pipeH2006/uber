package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.model.ActualizacionTelegram;
import com.uber.notificaciones.domain.model.gateway.TelegramGateway;
import com.uber.notificaciones.domain.model.gateway.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VincularTelegramUseCaseTest {

    @Mock
    private TelegramGateway telegramGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    private VincularTelegramUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new VincularTelegramUseCase(telegramGateway, usuarioGateway);
    }

    private void hayActualizaciones(ActualizacionTelegram... actualizaciones) {
        when(telegramGateway.obtenerActualizaciones(0)).thenReturn(List.of(actualizaciones));
    }

    @Test
    void start_conUsuarioValido_vinculaYConfirma() {
        hayActualizaciones(new ActualizacionTelegram(10, "555", "/start 7"));
        when(usuarioGateway.vincularTelegram(7L, "555")).thenReturn(true);

        long siguiente = useCase.procesarActualizaciones(0);

        assertThat(siguiente).isEqualTo(11);
        verify(telegramGateway).enviar("555", VincularTelegramUseCase.MENSAJE_VINCULADO);
    }

    @Test
    void start_usuarioInexistente_avisaAlUsuario() {
        hayActualizaciones(new ActualizacionTelegram(10, "555", "/start 99"));
        when(usuarioGateway.vincularTelegram(99L, "555")).thenReturn(false);

        useCase.procesarActualizaciones(0);

        verify(telegramGateway).enviar("555", VincularTelegramUseCase.MENSAJE_USUARIO_INEXISTENTE);
    }

    @Test
    void start_fallaMsUsuarios_avisaQueSeIntenteDeNuevo() {
        hayActualizaciones(new ActualizacionTelegram(10, "555", "/start 7"));
        when(usuarioGateway.vincularTelegram(7L, "555")).thenThrow(new IllegalStateException("caído"));

        useCase.procesarActualizaciones(0);

        verify(telegramGateway).enviar("555", VincularTelegramUseCase.MENSAJE_ERROR);
    }

    @Test
    void start_fallaElAvisoDeConfirmacion_noInterrumpeElProceso() {
        hayActualizaciones(new ActualizacionTelegram(10, "555", "/start 7"), new ActualizacionTelegram(11, "666", "/start 8"));
        when(usuarioGateway.vincularTelegram(anyLong(), anyString())).thenReturn(true);
        doThrow(new EnvioNotificacionException("fallo")).when(telegramGateway).enviar("555", VincularTelegramUseCase.MENSAJE_VINCULADO);

        long siguiente = useCase.procesarActualizaciones(0);

        assertThat(siguiente).isEqualTo(12);
        verify(usuarioGateway).vincularTelegram(8L, "666");
    }

    @Test
    void mensajesQueNoSonStartConUsuario_seIgnoranPeroAvanzaElOffset() {
        hayActualizaciones(
                new ActualizacionTelegram(20, "555", "hola"),
                new ActualizacionTelegram(22, "555", "/start abc"),
                new ActualizacionTelegram(23, null, null));

        long siguiente = useCase.procesarActualizaciones(0);

        assertThat(siguiente).isEqualTo(24);
        verifyNoInteractions(usuarioGateway);
        verify(telegramGateway, never()).enviar(anyString(), anyString());
    }

    @Test
    void sinNovedades_mantieneElOffset() {
        when(telegramGateway.obtenerActualizaciones(5)).thenReturn(List.of());

        assertThat(useCase.procesarActualizaciones(5)).isEqualTo(5);
    }

    @Test
    void startSinId_pideCompartirElContacto() {
        hayActualizaciones(new ActualizacionTelegram(30, "555", "/start"));

        useCase.procesarActualizaciones(0);

        verify(telegramGateway).solicitarContacto("555", VincularTelegramUseCase.MENSAJE_PEDIR_CONTACTO);
        verifyNoInteractions(usuarioGateway);
    }

    @Test
    void startSinId_siFallaElBoton_noInterrumpe() {
        hayActualizaciones(new ActualizacionTelegram(30, "555", "/start"));
        doThrow(new EnvioNotificacionException("fallo")).when(telegramGateway).solicitarContacto(anyString(), anyString());

        assertThat(useCase.procesarActualizaciones(0)).isEqualTo(31);
    }

    @Test
    void contactoPropio_vinculaPorTelefonoYConfirma() {
        hayActualizaciones(new ActualizacionTelegram(40, "555", null, "573001234567", true));
        when(usuarioGateway.vincularPorTelefono("573001234567", "555")).thenReturn(2);

        useCase.procesarActualizaciones(0);

        verify(telegramGateway).enviar("555", VincularTelegramUseCase.MENSAJE_VINCULADO);
    }

    @Test
    void contactoPropio_sinUsuarioConEseTelefono_avisa() {
        hayActualizaciones(new ActualizacionTelegram(40, "555", null, "573001234567", true));
        when(usuarioGateway.vincularPorTelefono("573001234567", "555")).thenReturn(0);

        useCase.procesarActualizaciones(0);

        verify(telegramGateway).enviar("555", VincularTelegramUseCase.MENSAJE_TELEFONO_SIN_USUARIO);
    }

    @Test
    void contactoPropio_fallaMsUsuarios_avisaQueSeIntenteDeNuevo() {
        hayActualizaciones(new ActualizacionTelegram(40, "555", null, "573001234567", true));
        when(usuarioGateway.vincularPorTelefono(anyString(), anyString())).thenThrow(new IllegalStateException("caído"));

        useCase.procesarActualizaciones(0);

        verify(telegramGateway).enviar("555", VincularTelegramUseCase.MENSAJE_ERROR);
    }

    @Test
    void contactoDeOtraPersona_noVincula() {
        hayActualizaciones(new ActualizacionTelegram(40, "555", null, "573001234567", false));

        useCase.procesarActualizaciones(0);

        verify(telegramGateway).enviar("555", VincularTelegramUseCase.MENSAJE_CONTACTO_AJENO);
        verifyNoInteractions(usuarioGateway);
    }
}
