package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.exception.EstadoPagoInvalidoException;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.gateway.PagoGateway;
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
class ReembolsarPagoUseCaseTest {
    @Mock
    private PagoGateway pagoGateway;

    private ReembolsarPagoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReembolsarPagoUseCase(pagoGateway);
    }

    @Test
    void reembolsar_pagoAprobado_loReembolsa() {
        Pago pago = Pago.builder().id(1L).estado(EstadoPago.APROBADO).build();
        when(pagoGateway.buscarPorId(1L)).thenReturn(Optional.of(pago));
        when(pagoGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = useCase.reembolsar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoPago.REEMBOLSADO);
    }

    @Test
    void reembolsar_pagoPendiente_lanzaExcepcion() {
        Pago pago = Pago.builder().id(1L).estado(EstadoPago.PENDIENTE).build();
        when(pagoGateway.buscarPorId(1L)).thenReturn(Optional.of(pago));

        assertThatThrownBy(() -> useCase.reembolsar(1L))
                .isInstanceOf(EstadoPagoInvalidoException.class);
    }

    @Test
    void reembolsar_pagoYaReembolsado_lanzaExcepcion() {
        Pago pago = Pago.builder().id(1L).estado(EstadoPago.REEMBOLSADO).build();
        when(pagoGateway.buscarPorId(1L)).thenReturn(Optional.of(pago));

        assertThatThrownBy(() -> useCase.reembolsar(1L))
                .isInstanceOf(EstadoPagoInvalidoException.class);
    }

    @Test
    void reembolsar_inexistente_lanzaExcepcion() {
        when(pagoGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.reembolsar(99L))
                .isInstanceOf(PagoNoEncontradoException.class);
    }
}
