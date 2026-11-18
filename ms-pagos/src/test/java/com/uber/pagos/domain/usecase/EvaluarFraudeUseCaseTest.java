package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResultadoFraude;
import com.uber.pagos.domain.model.gateway.FraudeGateway;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EvaluarFraudeUseCaseTest {

    @Mock
    private PagoGateway pagoGateway;

    @Mock
    private FraudeGateway fraudeGateway;

    private EvaluarFraudeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new EvaluarFraudeUseCase(pagoGateway, fraudeGateway);
    }

    @Test
    void evaluarFraude_pagoExistente_persisteRiesgoYDevuelveResultado() {
        Pago pago = Pago.builder().id(1L).viajeId(10L).usuarioId(2L).build();
        ResultadoFraude resultado = new ResultadoFraude("ALTO", 85, "Monto atípico");
        when(pagoGateway.buscarPorId(1L)).thenReturn(Optional.of(pago));
        when(fraudeGateway.evaluar(pago)).thenReturn(resultado);
        when(pagoGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        ResultadoFraude obtenido = useCase.evaluarFraude(1L);

        assertThat(obtenido).isEqualTo(resultado);
        assertThat(pago.getRiesgoFraude()).isEqualTo("ALTO");
        verify(pagoGateway).guardar(pago);
    }

    @Test
    void evaluarFraude_pagoInexistente_lanzaExcepcion() {
        when(pagoGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.evaluarFraude(99L))
                .isInstanceOf(PagoNoEncontradoException.class);
    }
}
