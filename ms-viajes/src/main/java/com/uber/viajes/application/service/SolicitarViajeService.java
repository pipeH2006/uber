package com.uber.viajes.application.service;

import com.uber.viajes.application.port.in.Estimacion;
import com.uber.viajes.application.port.in.EstimarViajeUseCase;
import com.uber.viajes.application.port.in.SolicitarViajeUseCase;
import com.uber.viajes.application.port.out.NotificacionPort;
import com.uber.viajes.application.port.out.ViajeRepositoryPort;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SolicitarViajeService implements SolicitarViajeUseCase {

    private final EstimarViajeUseCase estimarUseCase;
    private final NotificacionPort notificacion;
    private final ViajeRepositoryPort repository;

    @Override
    public Viaje solicitar(Long pasajeroId, Long conductorId, Coordenada origen, Coordenada destino) {
        // La estimación (ruta + tarifa USD/COP) se obtiene a través del caso de uso de estimación,
        // que a su vez invoca MapsClientPort.calcularRuta y TipoCambioPort.obtenerTipoCambio("COP").
        Estimacion estimacion = estimarUseCase.estimar(origen, destino);

        Viaje viaje = Viaje.builder()
                .pasajeroId(pasajeroId)
                .conductorId(conductorId)
                .origenLat(origen.lat())
                .origenLng(origen.lng())
                .destinoLat(destino.lat())
                .destinoLng(destino.lng())
                .distanciaKm(estimacion.distanciaKm())
                .duracionMin(estimacion.duracionMin())
                .costoEstimado(estimacion.costoUSD())
                .moneda("USD")
                .estado(EstadoViaje.ASIGNADO)
                .fechaSolicitud(LocalDateTime.now())
                .build();

        Viaje guardado = repository.guardar(viaje);
        notificacion.notificar("conductor-" + conductorId, "Se te ha asignado un nuevo viaje");
        return guardado;
    }
}
