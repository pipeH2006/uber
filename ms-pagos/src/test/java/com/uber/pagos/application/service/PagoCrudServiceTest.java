package com.uber.pagos.application.service;

import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.MetodoPago;
import com.uber.pagos.domain.model.Pago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PagoCrudServiceTest {

    @Mock
    private PagoRepositoryPort repository;

    private PagoCrudService service;

    @BeforeEach
    void setUp() {
        service = new PagoCrudService(repository);
    }

    @Test
    void crear_conValoresNulos_asignaMonedaEstadoYFechaPorDefecto() {
        Pago pago = Pago.builder()
                .viajeId(1L)
                .usuarioId(2L)
                .monto(new BigDecimal("100.00"))
                .metodoPago(MetodoPago.TARJETA)
                .build();
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = service.crear(pago);

        assertThat(resultado.getMoneda()).isEqualTo("USD");
        assertThat(resultado.getEstado()).isEqualTo(EstadoPago.PENDIENTE);
        assertThat(resultado.getFecha()).isNotNull();
    }

    @Test
    void crear_conMonedaVacia_asignaUSD() {
        Pago pago = Pago.builder()
                .viajeId(1L)
                .usuarioId(2L)
                .monto(new BigDecimal("100.00"))
                .moneda("")
                .metodoPago(MetodoPago.EFECTIVO)
                .build();
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = service.crear(pago);

        assertThat(resultado.getMoneda()).isEqualTo("USD");
    }

    @Test
    void crear_conValoresCompletos_losPreserva() {
        LocalDateTime fecha = LocalDateTime.now();
        Pago pago = Pago.builder()
                .viajeId(1L)
                .usuarioId(2L)
                .monto(new BigDecimal("100.00"))
                .moneda("COP")
                .metodoPago(MetodoPago.TARJETA)
                .estado(EstadoPago.APROBADO)
                .fecha(fecha)
                .build();
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = service.crear(pago);

        assertThat(resultado.getMoneda()).isEqualTo("COP");
        assertThat(resultado.getEstado()).isEqualTo(EstadoPago.APROBADO);
        assertThat(resultado.getFecha()).isEqualTo(fecha);
    }

    @Test
    void buscarPorId_existente_devuelvePago() {
        Pago pago = Pago.builder().id(1L).viajeId(10L).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(pago));

        assertThat(service.buscarPorId(1L)).isEqualTo(pago);
    }

    @Test
    void buscarPorId_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(PagoNoEncontradoException.class);
    }

    @Test
    void listar_devuelveTodos() {
        when(repository.listar()).thenReturn(List.of(new Pago(), new Pago()));

        assertThat(service.listar()).hasSize(2);
    }

    @Test
    void actualizar_cambiaCamposIndicados() {
        Pago existente = Pago.builder().id(1L).viajeId(10L).usuarioId(2L)
                .monto(new BigDecimal("100.00")).moneda("USD")
                .metodoPago(MetodoPago.TARJETA).estado(EstadoPago.PENDIENTE).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago datos = Pago.builder()
                .monto(new BigDecimal("200.00"))
                .moneda("COP")
                .metodoPago(MetodoPago.EFECTIVO)
                .estado(EstadoPago.APROBADO)
                .build();
        Pago resultado = service.actualizar(1L, datos);

        assertThat(resultado.getMonto()).isEqualByComparingTo("200.00");
        assertThat(resultado.getMoneda()).isEqualTo("COP");
        assertThat(resultado.getMetodoPago()).isEqualTo(MetodoPago.EFECTIVO);
        assertThat(resultado.getEstado()).isEqualTo(EstadoPago.APROBADO);
    }

    @Test
    void actualizar_sinCambios_devuelveExistente() {
        Pago existente = Pago.builder().id(1L).viajeId(10L).usuarioId(2L)
                .monto(new BigDecimal("100.00")).moneda("USD")
                .metodoPago(MetodoPago.TARJETA).estado(EstadoPago.PENDIENTE).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = service.actualizar(1L, Pago.builder().build());

        assertThat(resultado.getMonto()).isEqualByComparingTo("100.00");
        assertThat(resultado.getMoneda()).isEqualTo("USD");
        assertThat(resultado.getEstado()).isEqualTo(EstadoPago.PENDIENTE);
    }

    @Test
    void actualizar_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, Pago.builder().build()))
                .isInstanceOf(PagoNoEncontradoException.class);
    }

    @Test
    void eliminar_existente_elimina() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(new Pago()));

        service.eliminar(1L);

        verify(repository).eliminar(1L);
    }

    @Test
    void eliminar_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(PagoNoEncontradoException.class);
    }
}
