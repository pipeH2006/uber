package com.uber.pagos.application.service;

import com.uber.pagos.application.port.out.FraudePort;
import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResultadoFraude;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluarFraudeServiceTest {

    @Mock
    private PagoRepositoryPort repository;

    @Mock
    private FraudePort fraudePort;

    private EvaluarFraudeService service;

    @BeforeEach
    void setUp() {
        service = new EvaluarFraudeService(repository, fraudePort);
    }

    @Test
    void evaluarFraude_pagoExistente_persisteRiesgoYDevuelveResultado() {
        Pago pago = Pago.builder().id(1L).viajeId(10L).usuarioId(2L).build();
        ResultadoFraude resultado = new ResultadoFraude("ALTO", 85, "Monto atípico");
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(pago));
        when(fraudePort.evaluar(pago)).thenReturn(resultado);
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        ResultadoFraude obtenido = service.evaluarFraude(1L);

        assertThat(obtenido).isEqualTo(resultado);
        assertThat(pago.getRiesgoFraude()).isEqualTo("ALTO");
        verify(repository).guardar(pago);
    }

    @Test
    void evaluarFraude_pagoInexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.evaluarFraude(99L))
                .isInstanceOf(PagoNoEncontradoException.class);
    }
}
