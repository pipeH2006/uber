package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.EstadoViajeInvalidoException;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.NotificacionGateway;
import com.uber.viajes.domain.model.gateway.UsuarioGateway;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IniciarViajeUseCaseTest {

    @Mock
    private ViajeGateway viajeGateway;

    @Mock
    private NotificacionGateway notificacionGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    private IniciarViajeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new IniciarViajeUseCase(viajeGateway, notificacionGateway, usuarioGateway);
    }

    @Test
    void iniciar_asignado_pasaAEnCurso() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.ASIGNADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Viaje resultado = useCase.iniciar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoViaje.EN_CURSO);
        assertThat(resultado.getFechaInicio()).isNotNull();
    }

    @Test
    void iniciar_pasajeroConChat_loNotificaQueElViajeComenzo() {
        Viaje v = Viaje.builder().id(1L).pasajeroId(7L).estado(EstadoViaje.ASIGNADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioGateway.obtenerChatIdTelegram(7L)).thenReturn(Optional.of("777"));

        useCase.iniciar(1L);

        verify(notificacionGateway).notificar("777", "Tu viaje ha comenzado, ¡buen viaje!");
    }

    @Test
    void iniciar_conductorConChat_tambienRecibeConfirmacion() {
        Viaje v = Viaje.builder().id(1L).pasajeroId(7L).conductorId(8L).estado(EstadoViaje.ASIGNADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioGateway.obtenerChatIdTelegram(7L)).thenReturn(Optional.of("777"));
        when(usuarioGateway.obtenerChatIdTelegram(8L)).thenReturn(Optional.of("888"));

        useCase.iniciar(1L);

        verify(notificacionGateway).notificar("777", "Tu viaje ha comenzado, ¡buen viaje!");
        verify(notificacionGateway).notificar("888", "Comenzaste el viaje. ¡Buen camino!");
    }

    @Test
    void iniciar_pasajeroSinChat_noNotifica() {
        Viaje v = Viaje.builder().id(1L).pasajeroId(7L).estado(EstadoViaje.ASIGNADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioGateway.obtenerChatIdTelegram(7L)).thenReturn(Optional.empty());

        useCase.iniciar(1L);

        verifyNoInteractions(notificacionGateway);
    }

    @Test
    void iniciar_estadoInvalido_lanzaExcepcion() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.SOLICITADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> useCase.iniciar(1L))
                .isInstanceOf(EstadoViajeInvalidoException.class);
    }

    @Test
    void iniciar_inexistente_lanzaExcepcion() {
        when(viajeGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.iniciar(99L))
                .isInstanceOf(ViajeNoEncontradoException.class);
    }
}
