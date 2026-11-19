package com.uber.viajes.domain.model.gateway;

import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Ruta;

/**
 * Puerto de salida para el cálculo de rutas (distancia y duración).
 */
public interface RutasGateway {

    Ruta calcularRuta(Coordenada origen, Coordenada destino);
}
