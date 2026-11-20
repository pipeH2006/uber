package com.uber.usuarios.infraestructure.mapper;

import com.uber.usuarios.domain.model.Calificacion;
import com.uber.usuarios.infraestructure.driver_adapters.jpa_repository.CalificacionData;
import org.springframework.stereotype.Component;

@Component
public class MapperCalificacion {
    public Calificacion toCalificacion(CalificacionData data) {
        return Calificacion.builder()
                .id(data.getId())
                .usuarioId(data.getUsuarioId())
                .calificadorId(data.getCalificadorId())
                .puntaje(data.getPuntaje())
                .comentario(data.getComentario())
                .fecha(data.getFecha())
                .build();
    }

    public CalificacionData toData(Calificacion calificacion) {
        return CalificacionData.builder()
                .id(calificacion.getId())
                .usuarioId(calificacion.getUsuarioId())
                .calificadorId(calificacion.getCalificadorId())
                .puntaje(calificacion.getPuntaje())
                .comentario(calificacion.getComentario())
                .fecha(calificacion.getFecha())
                .build();
    }
}
