package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.in.NotificacionCrudUseCase;
import com.uber.notificaciones.application.port.out.NotificacionRepositoryPort;
import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificacionCrudService implements NotificacionCrudUseCase {

    private final NotificacionRepositoryPort repository;

    @Override
    public Notificacion crear(Notificacion notificacion) {
        if (notificacion.getDestinatario() == null || notificacion.getDestinatario().isBlank()) {
            throw new IllegalArgumentException("El destinatario es obligatorio");
        }
        if (notificacion.getMensaje() == null || notificacion.getMensaje().isBlank()) {
            throw new IllegalArgumentException("El mensaje es obligatorio");
        }
        notificacion.setCanal(notificacion.getCanal() == null ? Canal.TELEGRAM : notificacion.getCanal());
        notificacion.setTipo(notificacion.getTipo() == null ? TipoNotificacion.GENERICO : notificacion.getTipo());
        notificacion.setEstado(notificacion.getEstado() == null ? EstadoNotificacion.PENDIENTE : notificacion.getEstado());
        return repository.guardar(notificacion);
    }

    @Override
    public Notificacion buscarPorId(Long id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new NotificacionNoEncontradaException(id));
    }

    @Override
    public List<Notificacion> listar() {
        return repository.listar();
    }

    @Override
    public Notificacion actualizar(Long id, Notificacion datos) {
        Notificacion existente = buscarPorId(id);
        if (datos.getDestinatario() != null) {
            existente.setDestinatario(datos.getDestinatario());
        }
        if (datos.getMensaje() != null) {
            existente.setMensaje(datos.getMensaje());
        }
        if (datos.getTipo() != null) {
            existente.setTipo(datos.getTipo());
        }
        if (datos.getCanal() != null) {
            existente.setCanal(datos.getCanal());
        }
        if (datos.getEstado() != null) {
            existente.setEstado(datos.getEstado());
        }
        return repository.guardar(existente);
    }

    @Override
    public void eliminar(Long id) {
        if (repository.buscarPorId(id).isEmpty()) {
            throw new NotificacionNoEncontradaException(id);
        }
        repository.eliminar(id);
    }
}
