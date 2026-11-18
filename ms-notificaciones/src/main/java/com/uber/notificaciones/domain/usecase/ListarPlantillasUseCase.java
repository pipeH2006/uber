package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.model.Plantilla;
import com.uber.notificaciones.domain.model.gateway.PlantillaGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Caso de uso para listar las plantillas disponibles.
 */
@RequiredArgsConstructor
public class ListarPlantillasUseCase {

    private final PlantillaGateway plantillaGateway;

    public List<Plantilla> listarPlantillas() {
        return plantillaGateway.listar();
    }
}
