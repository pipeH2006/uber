package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.exception.ErrorExternoException;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.Factura;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.gateway.PagoGateway;
import com.uber.pagos.domain.model.gateway.TipoCambioGateway;
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
class GenerarFacturaUseCaseTest {
    @Mock
    private PagoGateway pagoGateway;

    @Mock
    private TipoCambioGateway tipoCambioGateway;

    private GenerarFacturaUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new GenerarFacturaUseCase(pagoGateway, tipoCambioGateway);
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
        when(pagoGateway.buscarPorId(1L)).thenReturn(Optional.of(pago));
        when(tipoCambioGateway.obtenerTipoCambio("COP")).thenReturn(new BigDecimal("4000.00"));

        Factura factura = useCase.generarFactura(1L);

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
        when(pagoGateway.buscarPorId(1L)).thenReturn(Optional.of(pago));
        when(tipoCambioGateway.obtenerTipoCambio("COP"))
                .thenThrow(new ErrorExternoException("Servicio no disponible"));

        assertThatThrownBy(() -> useCase.generarFactura(1L))
                .isInstanceOf(ErrorExternoException.class);
    }

    @Test
    void generarFactura_pagoInexistente_lanzaExcepcion() {
        when(pagoGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.generarFactura(99L))
                .isInstanceOf(PagoNoEncontradoException.class);
    }
}
