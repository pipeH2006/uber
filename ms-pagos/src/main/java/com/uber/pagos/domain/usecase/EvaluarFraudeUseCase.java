package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResultadoFraude;
import com.uber.pagos.domain.model.gateway.FraudeGateway;
import com.uber.pagos.domain.model.gateway.PagoGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class EvaluarFraudeUseCase {

    private final PagoGateway pagoGateway;
    private final FraudeGateway fraudeGateway;

    public ResultadoFraude evaluarFraude(Long id) {
        Pago pago = pagoGateway.buscarPorId(id)
                .orElseThrow(() -> new PagoNoEncontradoException(id));

        ResultadoFraude resultado = fraudeGateway.evaluar(pago);
        pago.setRiesgoFraude(resultado.riesgo());
        pagoGateway.guardar(pago);
        return resultado;
    }
}
