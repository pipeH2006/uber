package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import com.uber.notificaciones.domain.model.gateway.NotificacionGateway;
import lombok.RequiredArgsConstructor;

import java.util.List;

/**
 * Caso de uso CRUD del agregado Notificacion.
 */
@RequiredArgsConstructor
public class NotificacionCrudUseCase {

    private final NotificacionGateway notificacionGateway;

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
        return notificacionGateway.guardar(notificacion);
    }

    public Notificacion buscarPorId(Long id) {
        return notificacionGateway.buscarPorId(id)
                .orElseThrow(() -> new NotificacionNoEncontradaException(id));
    }

    public List<Notificacion> listar() {
        return notificacionGateway.listar();
    }

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
        return notificacionGateway.guardar(existente);
    }

    public void eliminar(Long id) {
        if (notificacionGateway.buscarPorId(id).isEmpty()) {
            throw new NotificacionNoEncontradaException(id);
        }
        notificacionGateway.eliminar(id);
    }
}
