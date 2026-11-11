package com.uber.viajes.application.port.out;

import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Ruta;

public interface MapsClientPort {

    Ruta calcularRuta(Coordenada origen, Coordenada destino);
}
