package com.uber.usuarios.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.usuarios.application.port.in.ActivarUsuarioUseCase;
import com.uber.usuarios.application.port.in.CalificarUsuarioUseCase;
import com.uber.usuarios.application.port.in.DesactivarUsuarioUseCase;
import com.uber.usuarios.application.port.in.LoginUseCase;
import com.uber.usuarios.application.port.in.ObtenerCalificacionPromedioUseCase;
import com.uber.usuarios.application.port.in.UsuarioCrudUseCase;
import com.uber.usuarios.application.port.in.VerificarDocumentoUseCase;
import com.uber.usuarios.application.port.in.CalificacionResumen;
import com.uber.usuarios.domain.exception.CredencialesInvalidasException;
import com.uber.usuarios.domain.exception.DocumentoNoVerificableException;
import com.uber.usuarios.domain.exception.EmailYaRegistradoException;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoDocumento;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Rol;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.infrastructure.adapter.in.rest.dto.RegistroUsuarioRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioCrudUseCase crudUseCase;

    @MockBean
    private LoginUseCase loginUseCase;

    @MockBean
    private ActivarUsuarioUseCase activarUseCase;

    @MockBean
    private DesactivarUsuarioUseCase desactivarUseCase;

    @MockBean
    private VerificarDocumentoUseCase verificarDocumentoUseCase;

    @MockBean
    private CalificarUsuarioUseCase calificarUseCase;

    @MockBean
    private ObtenerCalificacionPromedioUseCase promedioUseCase;

    private Usuario usuario() {
        return Usuario.builder()
                .id(1L)
                .nombre("Ana")
                .email("ana@correo.com")
                .telefono("3001234567")
                .rol(Rol.CONDUCTOR)
                .estado(EstadoUsuario.ACTIVO)
                .estadoDocumento(EstadoDocumento.VERIFICADO)
                .build();
    }

    @Test
    void registrar_devuelve201() throws Exception {
        Usuario u = usuario();
        when(crudUseCase.registrar(any())).thenReturn(u);

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistroUsuarioRequest(
                                "Ana", "ana@correo.com", "3001234567", "secreto", Rol.CONDUCTOR))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ana@correo.com"));
    }

    @Test
    void registrar_bodyInvalido_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"\",\"email\":\"no-es-email\",\"contrasena\":\"123\",\"rol\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorId_devuelve200() throws Exception {
        when(crudUseCase.buscarPorId(1L)).thenReturn(usuario());

        mockMvc.perform(get("/api/v1/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    void buscarPorId_inexistente_devuelve404() throws Exception {
        when(crudUseCase.buscarPorId(99L)).thenThrow(new UsuarioNoEncontradoException(99L));

        mockMvc.perform(get("/api/v1/usuarios/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listar_devuelve200() throws Exception {
        when(crudUseCase.listar()).thenReturn(List.of(usuario()));

        mockMvc.perform(get("/api/v1/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void actualizar_devuelve200() throws Exception {
        when(crudUseCase.actualizar(eq(1L), any())).thenReturn(usuario());

        mockMvc.perform(put("/api/v1/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ana Maria\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/v1/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void login_correcto_devuelve200() throws Exception {
        when(loginUseCase.login("ana@correo.com", "secreto")).thenReturn(usuario());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@correo.com\",\"contrasena\":\"secreto\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana@correo.com"));
    }

    @Test
    void login_credencialesInvalidas_devuelve401() throws Exception {
        when(loginUseCase.login("ana@correo.com", "mala"))
                .thenThrow(new CredencialesInvalidasException());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"ana@correo.com\",\"contrasena\":\"mala\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void activar_devuelve200() throws Exception {
        when(activarUseCase.activar(1L)).thenReturn(usuario());

        mockMvc.perform(post("/api/v1/usuarios/1/activar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    void activar_estadoInvalido_devuelve409() throws Exception {
        when(activarUseCase.activar(1L)).thenThrow(new EstadoInvalidoException("ya activo"));

        mockMvc.perform(post("/api/v1/usuarios/1/activar"))
                .andExpect(status().isConflict());
    }

    @Test
    void desactivar_devuelve200() throws Exception {
        when(desactivarUseCase.desactivar(1L)).thenReturn(usuario());

        mockMvc.perform(post("/api/v1/usuarios/1/desactivar"))
                .andExpect(status().isOk());
    }

    @Test
    void verificarDocumento_devuelve200() throws Exception {
        when(verificarDocumentoUseCase.verificarDocumento(1L)).thenReturn(usuario());

        mockMvc.perform(post("/api/v1/usuarios/1/verificar-documento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estadoDocumento").value("VERIFICADO"));
    }

    @Test
    void calificar_devuelve200() throws Exception {
        when(calificarUseCase.calificar(eq(1L), eq(2L), eq(5), any())).thenReturn(usuario());

        mockMvc.perform(post("/api/v1/usuarios/1/calificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"calificadorId\":2,\"puntaje\":5,\"comentario\":\"Buen viaje\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void calificar_puntajeInvalido_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/usuarios/1/calificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"calificadorId\":2,\"puntaje\":9}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void promedio_devuelve200() throws Exception {
        when(promedioUseCase.promedio(1L)).thenReturn(new CalificacionResumen(4.5, 2));

        mockMvc.perform(get("/api/v1/usuarios/1/calificacion-promedio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promedio").value(4.5))
                .andExpect(jsonPath("$.totalCalificaciones").value(2));
    }

    @Test
    void verificarDocumento_noConductor_devuelve422() throws Exception {
        when(verificarDocumentoUseCase.verificarDocumento(1L))
                .thenThrow(new DocumentoNoVerificableException(1L));

        mockMvc.perform(post("/api/v1/usuarios/1/verificar-documento"))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void registrar_emailDuplicado_devuelve409() throws Exception {
        when(crudUseCase.registrar(any()))
                .thenThrow(new EmailYaRegistradoException("ana@correo.com"));

        mockMvc.perform(post("/api/v1/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RegistroUsuarioRequest(
                                "Ana", "ana@correo.com", "3001234567", "secreto", Rol.PASAJERO))))
                .andExpect(status().isConflict());
    }

    @Test
    void argumentoInvalido_devuelve400() throws Exception {
        when(calificarUseCase.calificar(eq(1L), eq(2L), eq(5), any()))
                .thenThrow(new IllegalArgumentException("El puntaje debe estar entre 1 y 5"));

        mockMvc.perform(post("/api/v1/usuarios/1/calificar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"calificadorId\":2,\"puntaje\":5}"))
                .andExpect(status().isBadRequest());
    }
}
