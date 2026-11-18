package com.uber.notificaciones.infraestructure.driver_adapters.jpa_repository;

import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.gateway.NotificacionGateway;
import com.uber.notificaciones.infraestructure.mapper.MapperNotificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el gateway {@link NotificacionGateway}
 * delegando en Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class NotificacionDataGatewayImpl implements NotificacionGateway {

    private final NotificacionDataJpaRepository repository;
    private final MapperNotificacion mapperNotificacion;

    @Override
    public Notificacion guardar(Notificacion notificacion) {
        return mapperNotificacion.toNotificacion(repository.save(mapperNotificacion.toData(notificacion)));
    }

    @Override
    public Optional<Notificacion> buscarPorId(Long id) {
        return repository.findById(id).map(mapperNotificacion::toNotificacion);
    }

    @Override
    public List<Notificacion> listar() {
        return repository.findAll().stream().map(mapperNotificacion::toNotificacion).toList();
    }

    @Override
    public void eliminar(Long id) {
        repository.deleteById(id);
    }
}
