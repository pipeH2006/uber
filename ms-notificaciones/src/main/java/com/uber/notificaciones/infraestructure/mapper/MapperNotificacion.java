package com.uber.notificaciones.infraestructure.mapper;

import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.infraestructure.driver_adapters.jpa_repository.NotificacionData;
import org.springframework.stereotype.Component;

@Component
public class MapperNotificacion {

    public Notificacion toNotificacion(NotificacionData data) {
        return Notificacion.builder()
                .id(data.getId())
                .destinatario(data.getDestinatario())
                .canal(data.getCanal())
                .tipo(data.getTipo())
                .mensaje(data.getMensaje())
                .estado(data.getEstado())
                .fechaEnvio(data.getFechaEnvio())
                .intentos(data.getIntentos())
                .build();
    }

    public NotificacionData toData(Notificacion notificacion) {
        return NotificacionData.builder()
                .id(notificacion.getId())
                .destinatario(notificacion.getDestinatario())
                .canal(notificacion.getCanal())
                .tipo(notificacion.getTipo())
                .mensaje(notificacion.getMensaje())
                .estado(notificacion.getEstado())
                .fechaEnvio(notificacion.getFechaEnvio())
                .intentos(notificacion.getIntentos())
                .build();
    }
}
