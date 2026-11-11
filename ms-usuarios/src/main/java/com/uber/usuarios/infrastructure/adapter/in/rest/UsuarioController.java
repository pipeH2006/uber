package com.uber.usuarios.infrastructure.adapter.in.rest;

import com.uber.usuarios.application.port.in.ActivarUsuarioUseCase;
import com.uber.usuarios.application.port.in.CalificarUsuarioUseCase;
import com.uber.usuarios.application.port.in.DesactivarUsuarioUseCase;
import com.uber.usuarios.application.port.in.LoginUseCase;
import com.uber.usuarios.application.port.in.ObtenerCalificacionPromedioUseCase;
import com.uber.usuarios.application.port.in.UsuarioCrudUseCase;
import com.uber.usuarios.application.port.in.VerificarDocumentoUseCase;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.infrastructure.adapter.in.rest.dto.ActualizarUsuarioRequest;
import com.uber.usuarios.infrastructure.adapter.in.rest.dto.CalificacionRequest;
import com.uber.usuarios.infrastructure.adapter.in.rest.dto.LoginRequest;
import com.uber.usuarios.infrastructure.adapter.in.rest.dto.PromedioResponse;
import com.uber.usuarios.infrastructure.adapter.in.rest.dto.RegistroUsuarioRequest;
import com.uber.usuarios.infrastructure.adapter.in.rest.dto.UsuarioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
public class UsuarioController {

    private final UsuarioCrudUseCase crudUseCase;
    private final LoginUseCase loginUseCase;
    private final ActivarUsuarioUseCase activarUseCase;
    private final DesactivarUsuarioUseCase desactivarUseCase;
    private final VerificarDocumentoUseCase verificarDocumentoUseCase;
    private final CalificarUsuarioUseCase calificarUseCase;
    private final ObtenerCalificacionPromedioUseCase promedioUseCase;

    // ---- CRUD base ----

    @PostMapping("/usuarios")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        Usuario usuario = crudUseCase.registrar(toDomain(request));
        return ResponseEntity.created(URI.create("/api/v1/usuarios/" + usuario.getId()))
                .body(UsuarioResponse.from(usuario));
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponse.from(crudUseCase.buscarPorId(id)));
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> usuarios = crudUseCase.listar().stream()
                .map(UsuarioResponse::from)
                .toList();
        return ResponseEntity.ok(usuarios);
    }

    @PutMapping("/usuarios/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(@PathVariable Long id,
                                                      @Valid @RequestBody ActualizarUsuarioRequest request) {
        Usuario datos = Usuario.builder()
                .nombre(request.nombre())
                .email(request.email())
                .telefono(request.telefono())
                .rol(request.rol())
                .build();
        return ResponseEntity.ok(UsuarioResponse.from(crudUseCase.actualizar(id, datos)));
    }

    @DeleteMapping("/usuarios/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        crudUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ---- APIs de dominio (no CRUD) ----

    @PostMapping("/auth/login")
    public ResponseEntity<UsuarioResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(UsuarioResponse.from(loginUseCase.login(request.email(), request.contrasena())));
    }

    @PostMapping("/usuarios/{id}/activar")
    public ResponseEntity<UsuarioResponse> activar(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponse.from(activarUseCase.activar(id)));
    }

    @PostMapping("/usuarios/{id}/desactivar")
    public ResponseEntity<UsuarioResponse> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponse.from(desactivarUseCase.desactivar(id)));
    }

    @PostMapping("/usuarios/{id}/verificar-documento")
    public ResponseEntity<UsuarioResponse> verificarDocumento(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponse.from(verificarDocumentoUseCase.verificarDocumento(id)));
    }

    @PostMapping("/usuarios/{id}/calificar")
    public ResponseEntity<UsuarioResponse> calificar(@PathVariable Long id,
                                                     @Valid @RequestBody CalificacionRequest request) {
        Usuario usuario = calificarUseCase.calificar(
                id, request.calificadorId(), request.puntaje(), request.comentario());
        return ResponseEntity.status(HttpStatus.OK).body(UsuarioResponse.from(usuario));
    }

    @GetMapping("/usuarios/{id}/calificacion-promedio")
    public ResponseEntity<PromedioResponse> promedio(@PathVariable Long id) {
        var resumen = promedioUseCase.promedio(id);
        return ResponseEntity.ok(new PromedioResponse(id, resumen.promedio(), resumen.totalCalificaciones()));
    }

    private Usuario toDomain(RegistroUsuarioRequest r) {
        return Usuario.builder()
                .nombre(r.nombre())
                .email(r.email())
                .telefono(r.telefono())
                .contrasenaHash(r.contrasena())
                .rol(r.rol())
                .build();
    }
}
