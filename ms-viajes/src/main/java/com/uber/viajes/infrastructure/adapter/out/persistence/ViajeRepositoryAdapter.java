package com.uber.viajes.infrastructure.adapter.out.persistence;

import com.uber.viajes.application.port.out.ViajeRepositoryPort;
import com.uber.viajes.domain.model.Viaje;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador que implementa el puerto de salida {@link ViajeRepositoryPort}
 * delegando en Spring Data JPA.
 */
@Component
@RequiredArgsConstructor
public class ViajeRepositoryAdapter implements ViajeRepositoryPort {

    private final ViajeJpaRepository jpaRepository;

    @Override
    public Viaje guardar(Viaje viaje) {
        return toDomain(jpaRepository.save(toEntity(viaje)));
    }

    @Override
    public Optional<Viaje> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Viaje> listar() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }

    private ViajeJpaEntity toEntity(Viaje v) {
        return ViajeJpaEntity.builder()
                .id(v.getId())
                .pasajeroId(v.getPasajeroId())
                .conductorId(v.getConductorId())
                .origenLat(v.getOrigenLat())
                .origenLng(v.getOrigenLng())
                .origenDireccion(v.getOrigenDireccion())
                .destinoLat(v.getDestinoLat())
                .destinoLng(v.getDestinoLng())
                .destinoDireccion(v.getDestinoDireccion())
                .distanciaKm(v.getDistanciaKm())
                .duracionMin(v.getDuracionMin())
                .costoEstimado(v.getCostoEstimado())
                .costoFinal(v.getCostoFinal())
                .moneda(v.getMoneda())
                .estado(v.getEstado())
                .fechaSolicitud(v.getFechaSolicitud())
                .fechaInicio(v.getFechaInicio())
                .fechaFin(v.getFechaFin())
                .build();
    }

    private Viaje toDomain(ViajeJpaEntity e) {
        return Viaje.builder()
                .id(e.getId())
                .pasajeroId(e.getPasajeroId())
                .conductorId(e.getConductorId())
                .origenLat(e.getOrigenLat())
                .origenLng(e.getOrigenLng())
                .origenDireccion(e.getOrigenDireccion())
                .destinoLat(e.getDestinoLat())
                .destinoLng(e.getDestinoLng())
                .destinoDireccion(e.getDestinoDireccion())
                .distanciaKm(e.getDistanciaKm())
                .duracionMin(e.getDuracionMin())
                .costoEstimado(e.getCostoEstimado())
                .costoFinal(e.getCostoFinal())
                .moneda(e.getMoneda())
                .estado(e.getEstado())
                .fechaSolicitud(e.getFechaSolicitud())
                .fechaInicio(e.getFechaInicio())
                .fechaFin(e.getFechaFin())
                .build();
    }
}
