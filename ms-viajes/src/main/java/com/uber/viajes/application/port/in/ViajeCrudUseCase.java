package com.uber.viajes.application.port.in;

import com.uber.viajes.domain.model.Viaje;

import java.util.List;

public interface ViajeCrudUseCase {

    Viaje crear(Viaje viaje);

    Viaje buscarPorId(Long id);

    List<Viaje> listar();

    Viaje actualizar(Long id, Viaje datos);

    void eliminar(Long id);
}
