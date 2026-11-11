package com.uber.viajes.application.port.in;

import com.uber.viajes.domain.model.Viaje;

public interface IniciarViajeUseCase {

    Viaje iniciar(Long id);
}
