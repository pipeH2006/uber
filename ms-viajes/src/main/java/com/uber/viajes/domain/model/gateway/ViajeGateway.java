package com.uber.viajes.domain.model.gateway;

import com.uber.viajes.domain.model.Viaje;

import java.util.List;
import java.util.Optional;

public interface ViajeGateway {

    Viaje guardar(Viaje viaje);

    Optional<Viaje> buscarPorId(Long id);

    List<Viaje> listar();

    void eliminar(Long id);
}
