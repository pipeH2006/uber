package com.uber.notificaciones.domain.model.gateway;

import com.uber.notificaciones.domain.model.Plantilla;

import java.util.List;
import java.util.Optional;

public interface PlantillaGateway {
    Plantilla guardar(Plantilla plantilla);

    Optional<Plantilla> buscarPorCodigo(String codigo);

    List<Plantilla> listar();
}
