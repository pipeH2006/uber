package com.uber.notificaciones.application.port.in;

import com.uber.notificaciones.domain.model.Plantilla;

import java.util.List;

/**
 * Caso de uso para listar las plantillas disponibles.
 */
public interface ListarPlantillasUseCase {

    List<Plantilla> listarPlantillas();
}
