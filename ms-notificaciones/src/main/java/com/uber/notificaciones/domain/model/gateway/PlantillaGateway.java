package com.uber.notificaciones.domain.model.gateway;

import com.uber.notificaciones.domain.model.Plantilla;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (gateway) hacia la persistencia del agregado
 * Plantilla.
 */
public interface PlantillaGateway {

    Plantilla guardar(Plantilla plantilla);

    Optional<Plantilla> buscarPorCodigo(String codigo);

    List<Plantilla> listar();
}
