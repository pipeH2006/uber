package com.uber.viajes.infrastructure.adapter.in.rest;

import com.uber.viajes.application.port.in.CancelarViajeUseCase;
import com.uber.viajes.application.port.in.Estimacion;
import com.uber.viajes.application.port.in.EstimarViajeUseCase;
import com.uber.viajes.application.port.in.FinalizarViajeUseCase;
import com.uber.viajes.application.port.in.IniciarViajeUseCase;
import com.uber.viajes.application.port.in.Seguimiento;
import com.uber.viajes.application.port.in.SeguimientoViajeUseCase;
import com.uber.viajes.application.port.in.SolicitarViajeUseCase;
import com.uber.viajes.application.port.in.ViajeCrudUseCase;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.infrastructure.adapter.in.rest.dto.ActualizarViajeRequest;
import com.uber.viajes.infrastructure.adapter.in.rest.dto.CrearViajeRequest;
import com.uber.viajes.infrastructure.adapter.in.rest.dto.EstimarViajeRequest;
import com.uber.viajes.infrastructure.adapter.in.rest.dto.SolicitarViajeRequest;
import com.uber.viajes.infrastructure.adapter.in.rest.dto.ViajeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ViajeController {

    private final ViajeCrudUseCase crudUseCase;
    private final EstimarViajeUseCase estimarUseCase;
    private final SolicitarViajeUseCase solicitarUseCase;
    private final IniciarViajeUseCase iniciarUseCase;
    private final FinalizarViajeUseCase finalizarUseCase;
    private final CancelarViajeUseCase cancelarUseCase;
    private final SeguimientoViajeUseCase seguimientoUseCase;

    // ---- CRUD base ----

    @PostMapping("/viajes")
    public ResponseEntity<ViajeResponse> crear(@Valid @RequestBody CrearViajeRequest request) {
        Viaje viaje = crudUseCase.crear(toDomain(request));
        return ResponseEntity.created(URI.create("/api/v1/viajes/" + viaje.getId()))
                .body(ViajeResponse.from(viaje));
    }

    @GetMapping("/viajes/{id}")
    public ResponseEntity<ViajeResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ViajeResponse.from(crudUseCase.buscarPorId(id)));
    }

    @GetMapping("/viajes")
    public ResponseEntity<List<ViajeResponse>> listar() {
        List<ViajeResponse> viajes = crudUseCase.listar().stream()
                .map(ViajeResponse::from)
                .toList();
        return ResponseEntity.ok(viajes);
    }

    @PutMapping("/viajes/{id}")
    public ResponseEntity<ViajeResponse> actualizar(@PathVariable Long id,
                                                    @Valid @RequestBody ActualizarViajeRequest request) {
        Viaje datos = Viaje.builder()
                .pasajeroId(request.pasajeroId())
                .conductorId(request.conductorId())
                .origenDireccion(request.origenDireccion())
                .destinoDireccion(request.destinoDireccion())
                .costoEstimado(request.costoEstimado())
                .costoFinal(request.costoFinal())
                .moneda(request.moneda())
                .build();
        return ResponseEntity.ok(ViajeResponse.from(crudUseCase.actualizar(id, datos)));
    }

    @DeleteMapping("/viajes/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        crudUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ---- APIs de dominio (no CRUD) ----

    @PostMapping("/viajes/estimar")
    public ResponseEntity<Estimacion> estimar(@Valid @RequestBody EstimarViajeRequest request) {
        Estimacion estimacion = estimarUseCase.estimar(
                new Coordenada(request.origenLat(), request.origenLng()),
                new Coordenada(request.destinoLat(), request.destinoLng()));
        return ResponseEntity.ok(estimacion);
    }

    @PostMapping("/viajes/solicitar")
    public ResponseEntity<ViajeResponse> solicitar(@Valid @RequestBody SolicitarViajeRequest request) {
        Viaje viaje = solicitarUseCase.solicitar(
                request.pasajeroId(),
                request.conductorId(),
                new Coordenada(request.origenLat(), request.origenLng()),
                new Coordenada(request.destinoLat(), request.destinoLng()));
        return ResponseEntity.ok(ViajeResponse.from(viaje));
    }

    @PostMapping("/viajes/{id}/iniciar")
    public ResponseEntity<ViajeResponse> iniciar(@PathVariable Long id) {
        return ResponseEntity.ok(ViajeResponse.from(iniciarUseCase.iniciar(id)));
    }

    @PostMapping("/viajes/{id}/finalizar")
    public ResponseEntity<ViajeResponse> finalizar(@PathVariable Long id) {
        return ResponseEntity.ok(ViajeResponse.from(finalizarUseCase.finalizar(id)));
    }

    @PostMapping("/viajes/{id}/cancelar")
    public ResponseEntity<ViajeResponse> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(ViajeResponse.from(cancelarUseCase.cancelar(id)));
    }

    @GetMapping("/viajes/{id}/seguimiento")
    public ResponseEntity<Seguimiento> seguimiento(@PathVariable Long id) {
        return ResponseEntity.ok(seguimientoUseCase.seguir(id));
    }

    private Viaje toDomain(CrearViajeRequest r) {
        return Viaje.builder()
                .pasajeroId(r.pasajeroId())
                .conductorId(r.conductorId())
                .origenLat(r.origenLat())
                .origenLng(r.origenLng())
                .origenDireccion(r.origenDireccion())
                .destinoLat(r.destinoLat())
                .destinoLng(r.destinoLng())
                .destinoDireccion(r.destinoDireccion())
                .distanciaKm(r.distanciaKm())
                .duracionMin(r.duracionMin())
                .costoEstimado(r.costoEstimado())
                .costoFinal(r.costoFinal())
                .moneda(r.moneda())
                .build();
    }
}
