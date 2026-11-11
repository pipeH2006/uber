package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.in.ListarPlantillasUseCase;
import com.uber.notificaciones.application.port.out.PlantillaRepositoryPort;
import com.uber.notificaciones.domain.model.Plantilla;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarPlantillasService implements ListarPlantillasUseCase {

    private final PlantillaRepositoryPort repository;

    @Override
    public List<Plantilla> listarPlantillas() {
        return repository.listar();
    }
}
