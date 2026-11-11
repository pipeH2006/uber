package com.uber.viajes.application.port.in;

import com.uber.viajes.domain.model.Coordenada;

public interface EstimarViajeUseCase {

    Estimacion estimar(Coordenada origen, Coordenada destino);
}
