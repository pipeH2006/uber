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
class CancelarViajeServiceTest {

    @Mock
    private ViajeRepositoryPort repository;

    private CancelarViajeService service;

    @BeforeEach
    void setUp() {
        service = new CancelarViajeService(repository);
    }

    @Test
    void cancelar_estadoActivo_pasaACancelado() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.ASIGNADO).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(v));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Viaje resultado = service.cancelar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoViaje.CANCELADO);
    }

    @Test
    void cancelar_finalizado_lanzaExcepcion() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.FINALIZADO).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> service.cancelar(1L))
                .isInstanceOf(EstadoViajeInvalidoException.class);
    }

    @Test
    void cancelar_yaCancelado_lanzaExcepcion() {
        Viaje v = Viaje.builder().id(1L).estado(EstadoViaje.CANCELADO).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> service.cancelar(1L))
                .isInstanceOf(EstadoViajeInvalidoException.class);
    }

    @Test
    void cancelar_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.cancelar(99L))
                .isInstanceOf(ViajeNoEncontradoException.class);
    }
}
