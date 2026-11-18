package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResumenIngresos;
import com.uber.pagos.domain.model.gateway.PagoGateway;
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
class ResumenIngresosUseCaseTest {

    @Mock
    private PagoGateway pagoGateway;

    private ResumenIngresosUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ResumenIngresosUseCase(pagoGateway);
    }

    @Test
    void resumen_conPagosAprobados_sumaMontos() {
        LocalDate fecha = LocalDate.of(2026, 11, 11);
        when(pagoGateway.listarAprobadosPorFecha(fecha)).thenReturn(List.of(
                Pago.builder().monto(new BigDecimal("100.00")).build(),
                Pago.builder().monto(new BigDecimal("50.50")).build()
        ));

        ResumenIngresos resumen = useCase.resumen(fecha);

        assertThat(resumen.fecha()).isEqualTo(fecha);
        assertThat(resumen.totalUSD()).isEqualByComparingTo("150.50");
        assertThat(resumen.cantidadPagos()).isEqualTo(2);
    }

    @Test
    void resumen_sinPagos_devuelveCero() {
        LocalDate fecha = LocalDate.of(2026, 11, 11);
        when(pagoGateway.listarAprobadosPorFecha(fecha)).thenReturn(List.of());

        ResumenIngresos resumen = useCase.resumen(fecha);

        assertThat(resumen.totalUSD()).isEqualByComparingTo("0");
        assertThat(resumen.cantidadPagos()).isZero();
    }
}
