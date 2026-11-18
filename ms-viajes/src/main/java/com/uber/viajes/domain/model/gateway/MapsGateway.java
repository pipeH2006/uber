package com.uber.viajes.domain.model.gateway;

import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Ruta;

public interface MapsGateway {

    Ruta calcularRuta(Coordenada origen, Coordenada destino);
}
