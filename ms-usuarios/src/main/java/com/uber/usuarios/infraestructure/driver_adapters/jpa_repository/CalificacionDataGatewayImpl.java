package com.uber.usuarios.infraestructure.driver_adapters.jpa_repository;

import com.uber.usuarios.domain.model.Calificacion;
import com.uber.usuarios.domain.model.gateway.CalificacionGateway;
import com.uber.usuarios.infraestructure.mapper.MapperCalificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CalificacionDataGatewayImpl implements CalificacionGateway {

    private final CalificacionDataJpaRepository repository;
    private final MapperCalificacion mapperCalificacion;

    @Override
    public Calificacion guardar(Calificacion calificacion) {
        return mapperCalificacion.toCalificacion(repository.save(mapperCalificacion.toData(calificacion)));
    }

    @Override
    public List<Calificacion> buscarPorUsuarioId(Long usuarioId) {
        return repository.findByUsuarioId(usuarioId).stream()
                .map(mapperCalificacion::toCalificacion)
                .toList();
    }
}
