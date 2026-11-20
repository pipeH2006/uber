package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.model.Plantilla;
import com.uber.notificaciones.domain.model.gateway.PlantillaGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarPlantillasUseCaseTest {
    @Mock
    private PlantillaGateway plantillaGateway;

    private ListarPlantillasUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ListarPlantillasUseCase(plantillaGateway);
    }

    @Test
    void listarPlantillas_devuelveTodas() {
        when(plantillaGateway.listar()).thenReturn(List.of(new Plantilla(), new Plantilla()));

        assertThat(useCase.listarPlantillas()).hasSize(2);
    }
}
