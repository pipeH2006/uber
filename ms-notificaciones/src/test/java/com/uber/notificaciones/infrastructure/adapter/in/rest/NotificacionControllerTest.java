package com.uber.notificaciones.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.notificaciones.application.port.in.ConsultarEstadoNotificacionUseCase;
import com.uber.notificaciones.application.port.in.EnviarConPlantillaUseCase;
import com.uber.notificaciones.application.port.in.EnviarNotificacionMasivaUseCase;
import com.uber.notificaciones.application.port.in.EnviarNotificacionUseCase;
import com.uber.notificaciones.application.port.in.ListarPlantillasUseCase;
import com.uber.notificaciones.application.port.in.NotificacionCrudUseCase;
import com.uber.notificaciones.application.port.in.ReintentarNotificacionUseCase;
import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.exception.EstadoInvalidoException;
import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.exception.PlantillaNoEncontradaException;
import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.Plantilla;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificacionController.class)
class NotificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private NotificacionCrudUseCase crudUseCase;

    @MockBean
    private EnviarNotificacionUseCase enviarUseCase;

    @MockBean
    private EnviarNotificacionMasivaUseCase enviarMasivoUseCase;

    @MockBean
    private ConsultarEstadoNotificacionUseCase estadoUseCase;

    @MockBean
    private ReintentarNotificacionUseCase reintentarUseCase;

    @MockBean
    private ListarPlantillasUseCase listarPlantillasUseCase;

    @MockBean
    private EnviarConPlantillaUseCase enviarConPlantillaUseCase;

    private Notificacion notificacion() {
        return Notificacion.builder()
                .id(1L)
                .destinatario("123456")
                .canal(Canal.TELEGRAM)
                .tipo(TipoNotificacion.VIAJE_SOLICITADO)
                .mensaje("Tu viaje ha sido solicitado")
                .estado(EstadoNotificacion.ENVIADA)
                .fechaEnvio(LocalDateTime.now())
                .intentos(1)
                .build();
    }

    private Plantilla plantilla() {
        return Plantilla.builder()
                .id(1L)
                .codigo("BIENVENIDA")
                .asunto("Bienvenida")
                .mensaje("Hola {nombre}, bienvenido a Uber")
                .build();
    }

    // ---- CRUD ----

    @Test
    void crear_devuelve201() throws Exception {
        when(crudUseCase.crear(any())).thenReturn(notificacion());

        mockMvc.perform(post("/api/v1/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"destinatario\":\"123456\",\"mensaje\":\"Hola\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crear_destinatarioVacio_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"destinatario\":\"\",\"mensaje\":\"Hola\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void crear_argumentoInvalido_devuelve400() throws Exception {
        when(crudUseCase.crear(any())).thenThrow(new IllegalArgumentException("El mensaje es obligatorio"));

        mockMvc.perform(post("/api/v1/notificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"destinatario\":\"123456\",\"mensaje\":\"Hola\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorId_devuelve200() throws Exception {
        when(crudUseCase.buscarPorId(1L)).thenReturn(notificacion());

        mockMvc.perform(get("/api/v1/notificaciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Tu viaje ha sido solicitado"));
    }

    @Test
    void buscarPorId_inexistente_devuelve404() throws Exception {
        when(crudUseCase.buscarPorId(99L)).thenThrow(new NotificacionNoEncontradaException(99L));

        mockMvc.perform(get("/api/v1/notificaciones/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listar_devuelve200() throws Exception {
        when(crudUseCase.listar()).thenReturn(List.of(notificacion()));

        mockMvc.perform(get("/api/v1/notificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void actualizar_devuelve200() throws Exception {
        when(crudUseCase.actualizar(eq(1L), any())).thenReturn(notificacion());

        mockMvc.perform(put("/api/v1/notificaciones/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"destinatario\":\"123456\",\"mensaje\":\"Nuevo mensaje\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/v1/notificaciones/1"))
                .andExpect(status().isNoContent());
    }

    // ---- Envío ----

    @Test
    void enviar_devuelve200() throws Exception {
        when(enviarUseCase.enviar(eq("123456"), eq("Hola"), any())).thenReturn(notificacion());

        mockMvc.perform(post("/api/v1/notificaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"destinatario\":\"123456\",\"mensaje\":\"Hola\",\"tipo\":\"VIAJE_SOLICITADO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADA"));
    }

    @Test
    void enviar_falla_devuelve502() throws Exception {
        when(enviarUseCase.enviar(anyString(), anyString(), any()))
                .thenThrow(new EnvioNotificacionException("Error de Telegram"));

        mockMvc.perform(post("/api/v1/notificaciones/enviar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"destinatario\":\"123456\",\"mensaje\":\"Hola\"}"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void enviarMasivo_devuelve200() throws Exception {
        when(enviarMasivoUseCase.enviarMasivo(anyList(), eq("Hola"), any()))
                .thenReturn(List.of(notificacion(), notificacion()));

        mockMvc.perform(post("/api/v1/notificaciones/enviar-masivo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"destinatarios\":[\"111\",\"222\"],\"mensaje\":\"Hola\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void estado_devuelve200() throws Exception {
        when(estadoUseCase.estado(1L)).thenReturn(notificacion());

        mockMvc.perform(get("/api/v1/notificaciones/1/estado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ENVIADA"));
    }

    @Test
    void reintentar_devuelve200() throws Exception {
        when(reintentarUseCase.reintentar(1L)).thenReturn(notificacion());

        mockMvc.perform(post("/api/v1/notificaciones/1/reintentar"))
                .andExpect(status().isOk());
    }

    @Test
    void reintentar_noFallida_devuelve409() throws Exception {
        when(reintentarUseCase.reintentar(1L))
                .thenThrow(new EstadoInvalidoException("Solo se puede reintentar una notificación en estado FALLIDA"));

        mockMvc.perform(post("/api/v1/notificaciones/1/reintentar"))
                .andExpect(status().isConflict());
    }

    @Test
    void plantillas_devuelve200() throws Exception {
        when(listarPlantillasUseCase.listarPlantillas()).thenReturn(List.of(plantilla()));

        mockMvc.perform(get("/api/v1/notificaciones/plantillas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("BIENVENIDA"));
    }

    @Test
    void enviarConPlantilla_devuelve200() throws Exception {
        when(enviarConPlantillaUseCase.enviarConPlantilla(eq("BIENVENIDA"), eq("123456"), anyMap()))
                .thenReturn(notificacion());

        mockMvc.perform(post("/api/v1/notificaciones/enviar-con-plantilla")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"BIENVENIDA\",\"destinatario\":\"123456\",\"variables\":{\"nombre\":\"Ana\"}}"))
                .andExpect(status().isOk());
    }

    @Test
    void enviarConPlantilla_plantillaInexistente_devuelve404() throws Exception {
        when(enviarConPlantillaUseCase.enviarConPlantilla(eq("INEXISTENTE"), anyString(), any()))
                .thenThrow(new PlantillaNoEncontradaException("INEXISTENTE"));

        mockMvc.perform(post("/api/v1/notificaciones/enviar-con-plantilla")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigo\":\"INEXISTENTE\",\"destinatario\":\"123456\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reintentar_inexistente_devuelve404() throws Exception {
        when(reintentarUseCase.reintentar(99L)).thenThrow(new NotificacionNoEncontradaException(99L));

        mockMvc.perform(post("/api/v1/notificaciones/99/reintentar"))
                .andExpect(status().isNotFound());
    }
}
