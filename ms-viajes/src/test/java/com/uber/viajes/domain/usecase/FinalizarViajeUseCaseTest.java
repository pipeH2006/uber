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

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FinalizarViajeUseCaseTest {

    @Mock
    private ViajeGateway viajeGateway;

    @Mock
    private NotificacionGateway notificacionGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    private FinalizarViajeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FinalizarViajeUseCase(viajeGateway, notificacionGateway, usuarioGateway);
    }

    @Test
    void finalizar_enCurso_pasaAFinalizadoYNotificaPasajero() {
        Viaje v = Viaje.builder().id(1L).pasajeroId(7L).estado(EstadoViaje.EN_CURSO)
                .costoEstimado(new BigDecimal("13.50")).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioGateway.obtenerChatIdTelegram(7L)).thenReturn(Optional.of("777"));

        Viaje resultado = useCase.finalizar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoViaje.FINALIZADO);
        assertThat(resultado.getCostoFinal()).isEqualByComparingTo("13.50");
        assertThat(resultado.getFechaFin()).isNotNull();

        verify(notificacionGateway).notificar("777", "Tu viaje ha finalizado");
    }

    @Test
    void finalizar_conductorConChat_recibeSuGananciaEstimada() {
        Viaje v = Viaje.builder().id(1L).pasajeroId(7L).conductorId(8L).estado(EstadoViaje.EN_CURSO)
                .costoEstimado(new BigDecimal("13.50")).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioGateway.obtenerChatIdTelegram(7L)).thenReturn(Optional.of("777"));
        when(usuarioGateway.obtenerChatIdTelegram(8L)).thenReturn(Optional.of("888"));

        useCase.finalizar(1L);

        verify(notificacionGateway).notificar("777", "Tu viaje ha finalizado");
        verify(notificacionGateway).notificar("888", "Finalizaste el viaje. Ganancia estimada: 13.50 USD");
    }

    @Test
    void finalizar_pasajeroSinChat_finalizaSinNotificar() {
        Viaje v = Viaje.builder().id(1L).pasajeroId(7L).estado(EstadoViaje.EN_CURSO)
                .costoEstimado(new BigDecimal("13.50")).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioGateway.obtenerChatIdTelegram(7L)).thenReturn(Optional.empty());

        Viaje resultado = useCase.finalizar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoViaje.FINALIZADO);
        verifyNoInteractions(notificacionGateway);
    }

    @Test
    void finalizar_estadoInvalido_lanzaExcepcion() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.ASIGNADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> useCase.finalizar(1L))
                .isInstanceOf(EstadoViajeInvalidoException.class);
    }

    @Test
    void finalizar_inexistente_lanzaExcepcion() {
        when(viajeGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.finalizar(99L))
                .isInstanceOf(ViajeNoEncontradoException.class);
    }
}
