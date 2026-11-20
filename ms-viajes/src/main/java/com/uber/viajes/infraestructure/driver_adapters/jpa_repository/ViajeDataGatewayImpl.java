package com.uber.viajes.infraestructure.driver_adapters.jpa_repository;

import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import com.uber.viajes.infraestructure.mapper.MapperViaje;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ViajeDataGatewayImpl implements ViajeGateway {
    private final ViajeDataJpaRepository repository;
    private final MapperViaje mapperViaje;

    @Override
    public Viaje guardar(Viaje viaje) {
        return mapperViaje.toViaje(repository.save(mapperViaje.toData(viaje)));
    }

    @Override
    public Optional<Viaje> buscarPorId(Long id) {
        return repository.findById(id).map(mapperViaje::toViaje);
    }

    @Override
    public List<Viaje> listar() {
        return repository.findAll().stream().map(mapperViaje::toViaje).toList();
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
