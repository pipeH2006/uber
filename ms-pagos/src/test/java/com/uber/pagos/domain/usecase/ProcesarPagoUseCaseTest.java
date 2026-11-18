package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.MetodoPago;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.gateway.NotificacionGateway;
import com.uber.pagos.domain.model.gateway.PagoGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcesarPagoUseCaseTest {

    @Mock
    private PagoGateway pagoGateway;

    @Mock
    private NotificacionGateway notificacionGateway;

    private ProcesarPagoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProcesarPagoUseCase(pagoGateway, notificacionGateway);
    }

    @Test
    void procesar_pagoValido_apruebaYNotifica() {
        when(pagoGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = useCase.procesar(1L, 2L, new BigDecimal("100.50"), "USD", MetodoPago.TARJETA);

        assertThat(resultado.getEstado()).isEqualTo(EstadoPago.APROBADO);
        assertThat(resultado.getReferencia()).startsWith("PAG-");
        assertThat(resultado.getMoneda()).isEqualTo("USD");
        assertThat(resultado.getFecha()).isNotNull();
        verify(notificacionGateway).notificar(eq("usuario-2"), contains("Pago aprobado"));
    }

    @Test
    void procesar_monedaNula_asignaUSD() {
        when(pagoGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = useCase.procesar(1L, 2L, new BigDecimal("100.50"), null, MetodoPago.TARJETA);

        assertThat(resultado.getMoneda()).isEqualTo("USD");
    }

    @Test
    void procesar_monedaVacia_asignaUSD() {
        when(pagoGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = useCase.procesar(1L, 2L, new BigDecimal("100.50"), "", MetodoPago.TARJETA);

        assertThat(resultado.getMoneda()).isEqualTo("USD");
    }

    @Test
    void procesar_monedaIndicada_laPreserva() {
        when(pagoGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = useCase.procesar(1L, 2L, new BigDecimal("100.50"), "COP", MetodoPago.BILLETERA);

        assertThat(resultado.getMoneda()).isEqualTo("COP");
    }
}
