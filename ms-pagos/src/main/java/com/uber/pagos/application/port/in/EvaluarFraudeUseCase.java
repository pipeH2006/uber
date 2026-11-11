package com.uber.pagos.application.port.in;

import com.uber.pagos.domain.model.ResultadoFraude;

public interface EvaluarFraudeUseCase {

    ResultadoFraude evaluarFraude(Long id);
}
