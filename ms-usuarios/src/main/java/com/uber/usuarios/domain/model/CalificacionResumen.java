package com.uber.usuarios.domain.model;

/**
 * Resultado del cálculo de calificación promedio de un usuario.
 */
public record CalificacionResumen(double promedio, int totalCalificaciones) {
}
