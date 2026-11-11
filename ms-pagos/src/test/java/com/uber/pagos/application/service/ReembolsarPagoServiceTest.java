package com.uber.pagos.application.service;

import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.domain.exception.EstadoPagoInvalidoException;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.Pago;
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
class ReembolsarPagoServiceTest {

    @Mock
    private PagoRepositoryPort repository;

    private ReembolsarPagoService service;

    @BeforeEach
    void setUp() {
        service = new ReembolsarPagoService(repository);
    }

    @Test
    void reembolsar_pagoAprobado_loReembolsa() {
        Pago pago = Pago.builder().id(1L).estado(EstadoPago.APROBADO).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(pago));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Pago resultado = service.reembolsar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoPago.REEMBOLSADO);
    }

    @Test
    void reembolsar_pagoPendiente_lanzaExcepcion() {
        Pago pago = Pago.builder().id(1L).estado(EstadoPago.PENDIENTE).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(pago));

        assertThatThrownBy(() -> service.reembolsar(1L))
                .isInstanceOf(EstadoPagoInvalidoException.class);
    }

    @Test
    void reembolsar_pagoYaReembolsado_lanzaExcepcion() {
        Pago pago = Pago.builder().id(1L).estado(EstadoPago.REEMBOLSADO).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(pago));

        assertThatThrownBy(() -> service.reembolsar(1L))
                .isInstanceOf(EstadoPagoInvalidoException.class);
    }

    @Test
    void reembolsar_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.reembolsar(99L))
                .isInstanceOf(PagoNoEncontradoException.class);
    }
}
