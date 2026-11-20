package com.uber.notificaciones.infraestructure.driver_adapters.jpa_repository;

import com.uber.notificaciones.domain.model.Plantilla;
import com.uber.notificaciones.domain.model.gateway.PlantillaGateway;
import com.uber.notificaciones.infraestructure.mapper.MapperPlantilla;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlantillaDataGatewayImpl implements PlantillaGateway {
    private final PlantillaDataJpaRepository repository;
    private final MapperPlantilla mapperPlantilla;

    @Override
    public Plantilla guardar(Plantilla plantilla) {
        return mapperPlantilla.toPlantilla(repository.save(mapperPlantilla.toData(plantilla)));
    }

    @Override
    public Optional<Plantilla> buscarPorCodigo(String codigo) {
        return repository.findByCodigo(codigo).map(mapperPlantilla::toPlantilla);
    }

    @Override
    public List<Plantilla> listar() {
        return repository.findAll().stream().map(mapperPlantilla::toPlantilla).toList();
    }
}
