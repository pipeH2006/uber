package com.uber.notificaciones.infraestructure.mapper;

import com.uber.notificaciones.domain.model.Plantilla;
import com.uber.notificaciones.infraestructure.driver_adapters.jpa_repository.PlantillaData;
import org.springframework.stereotype.Component;

@Component
public class MapperPlantilla {
    public Plantilla toPlantilla(PlantillaData data) {
        return Plantilla.builder()
                .id(data.getId())
                .codigo(data.getCodigo())
                .asunto(data.getAsunto())
                .mensaje(data.getMensaje())
                .build();
    }

    public PlantillaData toData(Plantilla plantilla) {
        return PlantillaData.builder()
                .id(plantilla.getId())
                .codigo(plantilla.getCodigo())
                .asunto(plantilla.getAsunto())
                .mensaje(plantilla.getMensaje())
                .build();
    }
}
