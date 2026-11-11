package com.uber.pagos.application.service;

import com.uber.pagos.application.port.in.EvaluarFraudeUseCase;
import com.uber.pagos.application.port.out.FraudePort;
import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResultadoFraude;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvaluarFraudeService implements EvaluarFraudeUseCase {

    private final PagoRepositoryPort repository;
    private final FraudePort fraudePort;

    @Override
    public ResultadoFraude evaluarFraude(Long id) {
        Pago pago = repository.buscarPorId(id)
                .orElseThrow(() -> new PagoNoEncontradoException(id));

        ResultadoFraude resultado = fraudePort.evaluar(pago);
        pago.setRiesgoFraude(resultado.riesgo());
        repository.guardar(pago);
        return resultado;
    }
}
