package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.EstadoViajeInvalidoException;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CancelarViajeUseCaseTest {

    @Mock
    private ViajeGateway viajeGateway;

    private CancelarViajeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CancelarViajeUseCase(viajeGateway);
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
    void cancelar_finalizado_lanzaExcepcion() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.FINALIZADO).build();
        when(viajeGateway.buscarPorId(1L)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> useCase.cancelar(1L))
                .isInstanceOf(EstadoViajeInvalidoException.class);
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
