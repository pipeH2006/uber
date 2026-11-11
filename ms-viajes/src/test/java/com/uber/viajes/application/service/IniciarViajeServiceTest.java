package com.uber.viajes.application.service;

import com.uber.viajes.application.port.out.ViajeRepositoryPort;
import com.uber.viajes.domain.exception.EstadoViajeInvalidoException;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
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
class IniciarViajeServiceTest {

    @Mock
    private ViajeRepositoryPort repository;

    private IniciarViajeService service;

    @BeforeEach
    void setUp() {
        service = new IniciarViajeService(repository);
    }

    @Test
    void iniciar_asignado_pasaAEnCurso() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.ASIGNADO).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Viaje resultado = service.iniciar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoViaje.EN_CURSO);
        assertThat(resultado.getFechaInicio()).isNotNull();
    }

    @Test
    void iniciar_estadoInvalido_lanzaExcepcion() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.SOLICITADO).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> service.iniciar(1L))
                .isInstanceOf(EstadoViajeInvalidoException.class);
    }

    @Test
    void iniciar_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.iniciar(99L))
                .isInstanceOf(ViajeNoEncontradoException.class);
    }
}
