package com.uber.viajes.domain.model;

import java.util.List;

/**
 * Ruta por calles entre dos puntos: distancia, duración y los puntos del trazado.
 */
public record Ruta(double distanciaKm, double duracionMin, List<Coordenada> puntos) {

    public Ruta(double distanciaKm, double duracionMin) {
        this(distanciaKm, duracionMin, List.of());
    }
}
