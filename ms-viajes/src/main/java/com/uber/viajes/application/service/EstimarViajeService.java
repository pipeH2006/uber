package com.uber.viajes.application.service;

import com.uber.viajes.application.port.in.Estimacion;
import com.uber.viajes.application.port.in.EstimarViajeUseCase;
import com.uber.viajes.application.port.out.MapsClientPort;
import com.uber.viajes.application.port.out.TipoCambioPort;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Ruta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class EstimarViajeService implements EstimarViajeUseCase {

    private static final BigDecimal TARIFA_BASE = new BigDecimal("2.50");
    private static final BigDecimal TARIFA_POR_KM = new BigDecimal("0.80");
    private static final BigDecimal TARIFA_POR_MINUTO = new BigDecimal("0.15");

    private final MapsClientPort mapsClient;
    private final TipoCambioPort tipoCambioPort;

    @Override
    public Estimacion estimar(Coordenada origen, Coordenada destino) {
        Ruta ruta = mapsClient.calcularRuta(origen, destino);
        BigDecimal costoUSD = calcularCostoUSD(ruta.distanciaKm(), ruta.duracionMin());
        BigDecimal tipoCambio = tipoCambioPort.obtenerTipoCambio("COP");
        BigDecimal costoCOP = costoUSD.multiply(tipoCambio).setScale(2, RoundingMode.HALF_UP);
        return new Estimacion(ruta.distanciaKm(), ruta.duracionMin(), costoUSD, costoCOP, tipoCambio);
    }

    private BigDecimal calcularCostoUSD(double distanciaKm, double duracionMin) {
        return TARIFA_BASE
                .add(TARIFA_POR_KM.multiply(BigDecimal.valueOf(distanciaKm)))
                .add(TARIFA_POR_MINUTO.multiply(BigDecimal.valueOf(duracionMin)))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
