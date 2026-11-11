package com.uber.notificaciones.infrastructure.adapter.in.rest;

import com.uber.notificaciones.application.port.in.ConsultarEstadoNotificacionUseCase;
import com.uber.notificaciones.application.port.in.EnviarConPlantillaUseCase;
import com.uber.notificaciones.application.port.in.EnviarNotificacionMasivaUseCase;
import com.uber.notificaciones.application.port.in.EnviarNotificacionUseCase;
import com.uber.notificaciones.application.port.in.ListarPlantillasUseCase;
import com.uber.notificaciones.application.port.in.NotificacionCrudUseCase;
import com.uber.notificaciones.application.port.in.ReintentarNotificacionUseCase;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.infrastructure.adapter.in.rest.dto.EnviarRequest;
import com.uber.notificaciones.infrastructure.adapter.in.rest.dto.EnvioMasivoRequest;
import com.uber.notificaciones.infrastructure.adapter.in.rest.dto.EnvioPlantillaRequest;
import com.uber.notificaciones.infrastructure.adapter.in.rest.dto.NotificacionRequest;
import com.uber.notificaciones.infrastructure.adapter.in.rest.dto.NotificacionResponse;
import com.uber.notificaciones.infrastructure.adapter.in.rest.dto.PlantillaResponse;
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
public class NotificacionController {

    private final NotificacionCrudUseCase crudUseCase;
    private final EnviarNotificacionUseCase enviarUseCase;
    private final EnviarNotificacionMasivaUseCase enviarMasivoUseCase;
    private final ConsultarEstadoNotificacionUseCase estadoUseCase;
    private final ReintentarNotificacionUseCase reintentarUseCase;
    private final ListarPlantillasUseCase listarPlantillasUseCase;
    private final EnviarConPlantillaUseCase enviarConPlantillaUseCase;

    // ---- CRUD base ----

    @PostMapping("/notificaciones")
    public ResponseEntity<NotificacionResponse> crear(@Valid @RequestBody NotificacionRequest request) {
        Notificacion notificacion = crudUseCase.crear(toDomain(request));
        return ResponseEntity.created(URI.create("/api/v1/notificaciones/" + notificacion.getId()))
                .body(NotificacionResponse.from(notificacion));
    }

    @GetMapping("/notificaciones/{id}")
    public ResponseEntity<NotificacionResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(NotificacionResponse.from(crudUseCase.buscarPorId(id)));
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<List<NotificacionResponse>> listar() {
        List<NotificacionResponse> notificaciones = crudUseCase.listar().stream()
                .map(NotificacionResponse::from)
                .toList();
        return ResponseEntity.ok(notificaciones);
    }

    @PutMapping("/notificaciones/{id}")
    public ResponseEntity<NotificacionResponse> actualizar(@PathVariable Long id,
                                                           @Valid @RequestBody NotificacionRequest request) {
        return ResponseEntity.ok(NotificacionResponse.from(crudUseCase.actualizar(id, toDomain(request))));
    }

    @DeleteMapping("/notificaciones/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        crudUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ---- APIs de envío ----

    @PostMapping("/notificaciones/enviar")
    public ResponseEntity<NotificacionResponse> enviar(@Valid @RequestBody EnviarRequest request) {
        return ResponseEntity.ok(NotificacionResponse.from(
                enviarUseCase.enviar(request.destinatario(), request.mensaje(), request.tipo())));
    }

    @PostMapping("/notificaciones/enviar-masivo")
    public ResponseEntity<List<NotificacionResponse>> enviarMasivo(@Valid @RequestBody EnvioMasivoRequest request) {
        List<NotificacionResponse> notificaciones = enviarMasivoUseCase
                .enviarMasivo(request.destinatarios(), request.mensaje(), request.tipo())
                .stream()
                .map(NotificacionResponse::from)
                .toList();
        return ResponseEntity.ok(notificaciones);
    }

    @GetMapping("/notificaciones/{id}/estado")
    public ResponseEntity<NotificacionResponse> estado(@PathVariable Long id) {
        return ResponseEntity.ok(NotificacionResponse.from(estadoUseCase.estado(id)));
    }

    @PostMapping("/notificaciones/{id}/reintentar")
    public ResponseEntity<NotificacionResponse> reintentar(@PathVariable Long id) {
        return ResponseEntity.ok(NotificacionResponse.from(reintentarUseCase.reintentar(id)));
    }

    @GetMapping("/notificaciones/plantillas")
    public ResponseEntity<List<PlantillaResponse>> plantillas() {
        List<PlantillaResponse> plantillas = listarPlantillasUseCase.listarPlantillas().stream()
                .map(PlantillaResponse::from)
                .toList();
        return ResponseEntity.ok(plantillas);
    }

    @PostMapping("/notificaciones/enviar-con-plantilla")
    public ResponseEntity<NotificacionResponse> enviarConPlantilla(@Valid @RequestBody EnvioPlantillaRequest request) {
        return ResponseEntity.ok(NotificacionResponse.from(
                enviarConPlantillaUseCase.enviarConPlantilla(request.codigo(), request.destinatario(), request.variables())));
    }

    private Notificacion toDomain(NotificacionRequest r) {
        return Notificacion.builder()
                .destinatario(r.destinatario())
                .canal(r.canal())
                .tipo(r.tipo())
                .mensaje(r.mensaje())
                .estado(r.estado())
                .build();
    }
}
