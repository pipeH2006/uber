package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Estimacion;
import com.uber.viajes.domain.model.Ruta;
import com.uber.viajes.domain.model.gateway.RutasGateway;
import com.uber.viajes.domain.model.gateway.TipoCambioGateway;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

@RequiredArgsConstructor
public class EstimarViajeUseCase {
    private static final BigDecimal TARIFA_BASE = new BigDecimal("2.50");
    private static final BigDecimal TARIFA_POR_KM = new BigDecimal("0.80");
    private static final BigDecimal TARIFA_POR_MINUTO = new BigDecimal("0.15");

    private final RutasGateway rutasGateway;
    private final TipoCambioGateway tipoCambioGateway;

    public Estimacion estimar(Coordenada origen, Coordenada destino) {
        Ruta ruta = rutasGateway.calcularRuta(origen, destino);
        BigDecimal costoUSD = calcularCostoUSD(ruta.distanciaKm(), ruta.duracionMin());
        BigDecimal tipoCambio = tipoCambioGateway.obtenerTipoCambio("COP");
        BigDecimal costoCOP = costoUSD.multiply(tipoCambio).setScale(2, RoundingMode.HALF_UP);
        return new Estimacion(ruta.distanciaKm(), ruta.duracionMin(), costoUSD, costoCOP, tipoCambio, ruta.puntos());
    }

    private BigDecimal calcularCostoUSD(double distanciaKm, double duracionMin) {
        return TARIFA_BASE
                .add(TARIFA_POR_KM.multiply(BigDecimal.valueOf(distanciaKm)))
                .add(TARIFA_POR_MINUTO.multiply(BigDecimal.valueOf(duracionMin)))
                .setScale(2, RoundingMode.HALF_UP);
    }
}
