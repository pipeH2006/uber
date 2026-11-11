package com.uber.viajes.application.port.in;

import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Viaje;

public interface SolicitarViajeUseCase {

    Viaje solicitar(Long pasajeroId, Long conductorId, Coordenada origen, Coordenada destino);
}
