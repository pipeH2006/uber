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
class IniciarViajeUseCaseTest {

    @Mock
    private ViajeGateway viajeGateway;

    private IniciarViajeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new IniciarViajeUseCase(viajeGateway);
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
