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
class CancelarViajeUseCaseTest {
    @Mock
    private ViajeGateway viajeGateway;

    @Mock
    private NotificacionGateway notificacionGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    private CancelarViajeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CancelarViajeUseCase(viajeGateway, notificacionGateway, usuarioGateway);
    }

    @Test
    void cancelar_estadoActivo_pasaACancelado() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.ASIGNADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Viaje resultado = useCase.cancelar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoViaje.CANCELADO);
    }

    @Test
    void cancelar_conChats_avisaAlPasajeroYAlConductor() {
        Viaje v = Viaje.builder().id(1L).pasajeroId(7L).conductorId(8L).estado(EstadoViaje.ASIGNADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioGateway.obtenerChatIdTelegram(7L)).thenReturn(Optional.of("777"));
        when(usuarioGateway.obtenerChatIdTelegram(8L)).thenReturn(Optional.of("888"));

        useCase.cancelar(1L);

        verify(notificacionGateway).notificar("777", "Tu viaje fue cancelado");
        verify(notificacionGateway).notificar("888", "El viaje que tenías asignado fue cancelado");
    }

    @Test
    void cancelar_sinConductorNiChats_noNotifica() {
        Viaje v = Viaje.builder().id(1L).pasajeroId(7L).estado(EstadoViaje.SOLICITADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioGateway.obtenerChatIdTelegram(7L)).thenReturn(Optional.empty());

        useCase.cancelar(1L);

        verifyNoInteractions(notificacionGateway);
    }

    @Test
    void cancelar_finalizado_lanzaExcepcion() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.FINALIZADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> useCase.cancelar(1L))
                .isInstanceOf(EstadoViajeInvalidoException.class);
    }

    @Test
    void cancelar_viajeEnCurso_lanzaExcepcionYNoNotifica() {
        Viaje v = Viaje.builder().id(1L).pasajeroId(7L).conductorId(8L).estado(EstadoViaje.EN_CURSO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> useCase.cancelar(1L))
                .isInstanceOf(EstadoViajeInvalidoException.class)
                .hasMessageContaining("EN_CURSO");

        verifyNoInteractions(notificacionGateway);
        assertThat(v.getEstado()).isEqualTo(EstadoViaje.EN_CURSO);
    }

    @Test
    void cancelar_yaCancelado_lanzaExcepcion() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.CANCELADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> useCase.cancelar(1L))
                .isInstanceOf(EstadoViajeInvalidoException.class);
    }

    @Test
    void cancelar_inexistente_lanzaExcepcion() {
        when(viajeGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.cancelar(99L))
                .isInstanceOf(ViajeNoEncontradoException.class);
    }
}
