package com.uber.pagos.application.service;

import com.uber.pagos.application.port.in.ResumenIngresos;
import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.domain.model.Pago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResumenIngresosServiceTest {

    @Mock
    private PagoRepositoryPort repository;

    private ResumenIngresosService service;

    @BeforeEach
    void setUp() {
        service = new ResumenIngresosService(repository);
    }

    @Test
    void resumen_conPagosAprobados_sumaMontos() {
        LocalDate fecha = LocalDate.of(2026, 11, 11);
        when(repository.listarAprobadosPorFecha(fecha)).thenReturn(List.of(
                Pago.builder().monto(new BigDecimal("100.00")).build(),
                Pago.builder().monto(new BigDecimal("50.50")).build()
        ));

        ResumenIngresos resumen = service.resumen(fecha);

        assertThat(resumen.fecha()).isEqualTo(fecha);
        assertThat(resumen.totalUSD()).isEqualByComparingTo("150.50");
        assertThat(resumen.cantidadPagos()).isEqualTo(2);
    }

    @Test
    void resumen_sinPagos_devuelveCero() {
        LocalDate fecha = LocalDate.of(2026, 11, 11);
        when(repository.listarAprobadosPorFecha(fecha)).thenReturn(List.of());

        ResumenIngresos resumen = service.resumen(fecha);

        assertThat(resumen.totalUSD()).isEqualByComparingTo("0");
        assertThat(resumen.cantidadPagos()).isZero();
    }
}
