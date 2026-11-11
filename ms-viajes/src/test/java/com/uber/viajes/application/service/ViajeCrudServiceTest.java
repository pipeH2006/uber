package com.uber.viajes.application.service;

import com.uber.viajes.application.port.out.ViajeRepositoryPort;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ViajeCrudServiceTest {

    @Mock
    private ViajeRepositoryPort repository;

    private ViajeCrudService service;

    @BeforeEach
    void setUp() {
        service = new ViajeCrudService(repository);
    }

    @Test
    void crear_asignaEstadoSolicitadoYMonedaDefault() {
        Viaje v = Viaje.builder().pasajeroId(1L).conductorId(2L).build();
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Viaje resultado = service.crear(v);

        assertThat(resultado.getEstado()).isEqualTo(EstadoViaje.SOLICITADO);
        assertThat(resultado.getMoneda()).isEqualTo("USD");
        assertThat(resultado.getFechaSolicitud()).isNotNull();
    }

    @Test
    void crear_monedaVacia_asignaUsd() {
        Viaje v = Viaje.builder().pasajeroId(1L).conductorId(2L).moneda("").build();
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Viaje resultado = service.crear(v);

        assertThat(resultado.getMoneda()).isEqualTo("USD");
    }

    @Test
    void buscarPorId_existente_devuelveViaje() {
        Viaje v = Viaje.builder().id(1L).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(v));

        assertThat(service.buscarPorId(1L)).isEqualTo(v);
    }

    @Test
    void buscarPorId_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(ViajeNoEncontradoException.class);
    }

    @Test
    void listar_devuelveTodos() {
        when(repository.listar()).thenReturn(List.of(new Viaje(), new Viaje()));

        assertThat(service.listar()).hasSize(2);
    }

    @Test
    void actualizar_cambiaCamposPermitidos() {
        Viaje existente = Viaje.builder().id(1L).pasajeroId(1L).conductorId(2L)
                .origenDireccion("A").moneda("USD").build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Viaje datos = Viaje.builder()
                .pasajeroId(10L)
                .conductorId(3L)
                .origenDireccion("O")
                .destinoDireccion("B")
                .costoEstimado(new BigDecimal("20.00"))
                .costoFinal(new BigDecimal("21.00"))
                .moneda("COP")
                .build();
        Viaje resultado = service.actualizar(1L, datos);

        assertThat(resultado.getPasajeroId()).isEqualTo(10L);
        assertThat(resultado.getConductorId()).isEqualTo(3L);
        assertThat(resultado.getOrigenDireccion()).isEqualTo("O");
        assertThat(resultado.getDestinoDireccion()).isEqualTo("B");
        assertThat(resultado.getCostoEstimado()).isEqualByComparingTo("20.00");
        assertThat(resultado.getCostoFinal()).isEqualByComparingTo("21.00");
        assertThat(resultado.getMoneda()).isEqualTo("COP");
    }

    @Test
    void actualizar_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, new Viaje()))
                .isInstanceOf(ViajeNoEncontradoException.class);
    }

    @Test
    void eliminar_existente_elimina() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(new Viaje()));

        service.eliminar(1L);

        verify(repository).eliminar(1L);
    }

    @Test
    void eliminar_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(ViajeNoEncontradoException.class);
    }
}
