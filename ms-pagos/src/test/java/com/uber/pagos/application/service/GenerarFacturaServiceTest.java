package com.uber.pagos.application.service;

import com.uber.pagos.application.port.in.Factura;
import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.application.port.out.TipoCambioPort;
import com.uber.pagos.domain.exception.ErrorExternoException;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.Pago;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GenerarFacturaServiceTest {

    @Mock
    private PagoRepositoryPort repository;

    @Mock
    private TipoCambioPort tipoCambioPort;

    private GenerarFacturaService service;

    @BeforeEach
    void setUp() {
        service = new GenerarFacturaService(repository, tipoCambioPort);
    }

    @Test
    void generarFactura_pagoExistente_calculaMontoEnCOP() {
        Pago pago = Pago.builder()
                .id(1L)
                .viajeId(10L)
                .monto(new BigDecimal("100.00"))
                .moneda("USD")
                .estado(EstadoPago.APROBADO)
                .referencia("PAG-123")
                .build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(pago));
        when(tipoCambioPort.obtenerTipoCambio("COP")).thenReturn(new BigDecimal("4000.00"));

        Factura factura = service.generarFactura(1L);

        assertThat(factura.pagoId()).isEqualTo(1L);
        assertThat(factura.viajeId()).isEqualTo(10L);
        assertThat(factura.montoCOP()).isEqualByComparingTo("400000.00");
        assertThat(factura.tipoCambio()).isEqualByComparingTo("4000.00");
        assertThat(factura.estado()).isEqualTo(EstadoPago.APROBADO);
        assertThat(factura.referencia()).isEqualTo("PAG-123");
    }

    @Test
    void generarFactura_errorExternoLanzaErrorExterno() {
        Pago pago = Pago.builder().id(1L).monto(new BigDecimal("100.00")).moneda("USD").build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(pago));
        when(tipoCambioPort.obtenerTipoCambio("COP"))
                .thenThrow(new ErrorExternoException("Servicio no disponible"));

        assertThatThrownBy(() -> service.generarFactura(1L))
                .isInstanceOf(ErrorExternoException.class);
    }

    @Test
    void generarFactura_pagoInexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.generarFactura(99L))
                .isInstanceOf(PagoNoEncontradoException.class);
    }
}
