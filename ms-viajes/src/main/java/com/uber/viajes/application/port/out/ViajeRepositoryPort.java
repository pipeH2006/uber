package com.uber.viajes.application.port.out;

import com.uber.viajes.domain.model.Viaje;

import java.util.List;
import java.util.Optional;

public interface ViajeRepositoryPort {

    Viaje guardar(Viaje viaje);

    Optional<Viaje> buscarPorId(Long id);

    List<Viaje> listar();

    void eliminar(Long id);
}
