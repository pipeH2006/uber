package com.uber.usuarios.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Calificación otorgada a un usuario (pasajero o conductor) por otro usuario.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion {

    private Long id;
    private Long usuarioId;
    private Long calificadorId;
    private Integer puntaje;
    private String comentario;
    private LocalDateTime fecha;
}
