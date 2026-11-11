package com.uber.pagos.application.service;

import com.uber.pagos.application.port.out.NotificacionPort;
import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.MetodoPago;
import com.uber.pagos.domain.model.Pago;
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
class ProcesarPagoServiceTest {

    @Mock
    private PagoRepositoryPort repository;

    @Mock
    private NotificacionPort notificacionPort;

    private ProcesarPagoService service;

    @BeforeEach
    void setUp() {
        service = new ProcesarPagoService(repository, notificacionPort);
    }

    @Test
    void procesar_pagoValido_apruebaYNotifica() {
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = service.procesar(1L, 2L, new BigDecimal("100.50"), "USD", MetodoPago.TARJETA);

        assertThat(resultado.getEstado()).isEqualTo(EstadoPago.APROBADO);
        assertThat(resultado.getReferencia()).startsWith("PAG-");
        assertThat(resultado.getMoneda()).isEqualTo("USD");
        assertThat(resultado.getFecha()).isNotNull();
        verify(notificacionPort).notificar(eq("usuario-2"), contains("Pago aprobado"));
    }

    @Test
    void procesar_monedaNula_asignaUSD() {
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = service.procesar(1L, 2L, new BigDecimal("100.50"), null, MetodoPago.TARJETA);

        assertThat(resultado.getMoneda()).isEqualTo("USD");
    }

    @Test
    void procesar_monedaVacia_asignaUSD() {
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = service.procesar(1L, 2L, new BigDecimal("100.50"), "", MetodoPago.TARJETA);

        assertThat(resultado.getMoneda()).isEqualTo("USD");
    }

    @Test
    void procesar_monedaIndicada_laPreserva() {
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = service.procesar(1L, 2L, new BigDecimal("100.50"), "COP", MetodoPago.BILLETERA);

        assertThat(resultado.getMoneda()).isEqualTo("COP");
    }
}
