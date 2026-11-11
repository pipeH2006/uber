package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.out.PlantillaRepositoryPort;
import com.uber.notificaciones.domain.model.Plantilla;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarPlantillasServiceTest {

    @Mock
    private PlantillaRepositoryPort repository;

    private ListarPlantillasService service;

    @BeforeEach
    void setUp() {
        service = new ListarPlantillasService(repository);
    }

    @Test
    void listarPlantillas_devuelveTodas() {
        when(repository.listar()).thenReturn(List.of(new Plantilla(), new Plantilla()));

        assertThat(service.listarPlantillas()).hasSize(2);
    }
}
