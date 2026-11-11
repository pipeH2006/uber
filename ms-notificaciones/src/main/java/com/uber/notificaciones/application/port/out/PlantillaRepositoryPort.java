package com.uber.notificaciones.application.port.out;

import com.uber.notificaciones.domain.model.Plantilla;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (driven port) hacia la persistencia del agregado
 * Plantilla.
 */
public interface PlantillaRepositoryPort {

    Plantilla guardar(Plantilla plantilla);

    Optional<Plantilla> buscarPorCodigo(String codigo);

    List<Plantilla> listar();
}
