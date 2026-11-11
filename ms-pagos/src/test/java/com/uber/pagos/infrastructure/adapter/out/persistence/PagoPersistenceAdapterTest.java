package com.uber.pagos.infrastructure.adapter.out.persistence;

import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.MetodoPago;
import com.uber.pagos.domain.model.Pago;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(PagoRepositoryAdapter.class)
class PagoPersistenceAdapterTest {

    @Autowired
    private PagoRepositoryAdapter adapter;

    private Pago pagoDomain() {
        return Pago.builder()
                .viajeId(10L)
                .usuarioId(2L)
                .monto(new BigDecimal("100.50"))
                .moneda("USD")
                .metodoPago(MetodoPago.TARJETA)
                .estado(EstadoPago.APROBADO)
                .fecha(LocalDateTime.of(2026, 11, 11, 10, 0))
                .referencia("PAG-123")
                .riesgoFraude("BAJO")
                .build();
    }

    @Test
    void guardarYBbuscarPorId_mapeaCorrectamente() {
        Pago guardado = adapter.guardar(pagoDomain());

        Optional<Pago> encontrado = adapter.buscarPorId(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getViajeId()).isEqualTo(10L);
        assertThat(encontrado.get().getUsuarioId()).isEqualTo(2L);
        assertThat(encontrado.get().getMonto()).isEqualByComparingTo("100.50");
        assertThat(encontrado.get().getMoneda()).isEqualTo("USD");
        assertThat(encontrado.get().getMetodoPago()).isEqualTo(MetodoPago.TARJETA);
        assertThat(encontrado.get().getEstado()).isEqualTo(EstadoPago.APROBADO);
        assertThat(encontrado.get().getReferencia()).isEqualTo("PAG-123");
        assertThat(encontrado.get().getRiesgoFraude()).isEqualTo("BAJO");
    }

    @Test
    void listar_devuelveTodosLosPagos() {
        adapter.guardar(pagoDomain());
        adapter.guardar(Pago.builder()
                .viajeId(11L)
                .usuarioId(3L)
                .monto(new BigDecimal("50.00"))
                .moneda("USD")
                .metodoPago(MetodoPago.EFECTIVO)
                .estado(EstadoPago.PENDIENTE)
                .fecha(LocalDateTime.now())
                .build());

        List<Pago> pagos = adapter.listar();

        assertThat(pagos).hasSize(2);
    }

    @Test
    void eliminar_borraElPago() {
        Pago guardado = adapter.guardar(pagoDomain());

        adapter.eliminar(guardado.getId());

        assertThat(adapter.buscarPorId(guardado.getId())).isEmpty();
    }

    @Test
    void listarAprobadosPorFecha_filtraPorEstadoYFecha() {
        LocalDate fecha = LocalDate.of(2026, 11, 11);
        adapter.guardar(pagoDomain());
        adapter.guardar(Pago.builder()
                .viajeId(12L)
                .usuarioId(4L)
                .monto(new BigDecimal("25.00"))
                .moneda("USD")
                .metodoPago(MetodoPago.BILLETERA)
                .estado(EstadoPago.APROBADO)
                .fecha(LocalDateTime.of(2026, 11, 11, 18, 0))
                .build());
        // No debe aparecer: otro estado y otra fecha
        adapter.guardar(Pago.builder()
                .viajeId(13L)
                .usuarioId(5L)
                .monto(new BigDecimal("99.00"))
                .moneda("USD")
                .metodoPago(MetodoPago.TARJETA)
                .estado(EstadoPago.PENDIENTE)
                .fecha(LocalDateTime.of(2026, 11, 11, 9, 0))
                .build());
        adapter.guardar(Pago.builder()
                .viajeId(14L)
                .usuarioId(6L)
                .monto(new BigDecimal("99.00"))
                .moneda("USD")
                .metodoPago(MetodoPago.TARJETA)
                .estado(EstadoPago.APROBADO)
                .fecha(LocalDateTime.of(2026, 11, 12, 9, 0))
                .build());

        List<Pago> aprobados = adapter.listarAprobadosPorFecha(fecha);

        assertThat(aprobados).hasSize(2);
        assertThat(aprobados).allMatch(p -> p.getEstado() == EstadoPago.APROBADO);
    }
}
