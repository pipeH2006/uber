package com.uber.viajes.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.viajes.application.port.in.CancelarViajeUseCase;
import com.uber.viajes.application.port.in.Estimacion;
import com.uber.viajes.application.port.in.EstimarViajeUseCase;
import com.uber.viajes.application.port.in.FinalizarViajeUseCase;
import com.uber.viajes.application.port.in.IniciarViajeUseCase;
import com.uber.viajes.application.port.in.Seguimiento;
import com.uber.viajes.application.port.in.SeguimientoViajeUseCase;
import com.uber.viajes.application.port.in.SolicitarViajeUseCase;
import com.uber.viajes.application.port.in.ViajeCrudUseCase;
import com.uber.viajes.domain.exception.ErrorExternoException;
import com.uber.viajes.domain.exception.EstadoViajeInvalidoException;
import com.uber.viajes.domain.exception.ViajeNoEncontradoException;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.infrastructure.adapter.in.rest.dto.CrearViajeRequest;
import com.uber.viajes.infrastructure.adapter.in.rest.dto.EstimarViajeRequest;
import com.uber.viajes.infrastructure.adapter.in.rest.dto.SolicitarViajeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
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

@WebMvcTest(ViajeController.class)
class ViajeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ViajeCrudUseCase crudUseCase;

    @MockBean
    private EstimarViajeUseCase estimarUseCase;

    @MockBean
    private SolicitarViajeUseCase solicitarUseCase;

    @MockBean
    private IniciarViajeUseCase iniciarUseCase;

    @MockBean
    private FinalizarViajeUseCase finalizarUseCase;

    @MockBean
    private CancelarViajeUseCase cancelarUseCase;

    @MockBean
    private SeguimientoViajeUseCase seguimientoUseCase;

    private Viaje viaje() {
        return Viaje.builder()
                .id(1L)
                .pasajeroId(10L)
                .conductorId(20L)
                .origenLat(4.71)
                .origenLng(-74.07)
                .origenDireccion("Cra 7 # 71-21")
                .destinoLat(4.60)
                .destinoLng(-74.08)
                .destinoDireccion("Aeropuerto El Dorado")
                .distanciaKm(12.5)
                .duracionMin(20.0)
                .costoEstimado(new BigDecimal("13.50"))
                .costoFinal(new BigDecimal("13.50"))
                .moneda("USD")
                .estado(EstadoViaje.ASIGNADO)
                .build();
    }

    @Test
    void crear_devuelve201() throws Exception {
        when(crudUseCase.crear(any())).thenReturn(viaje());

        mockMvc.perform(post("/api/v1/viajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CrearViajeRequest(
                                10L, 20L, 4.71, -74.07, "Cra 7", 4.60, -74.08, "Aeropuerto",
                                12.5, 20.0, new BigDecimal("13.50"), null, null))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("ASIGNADO"));
    }

    @Test
    void crear_bodyInvalido_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/viajes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conductorId\":20}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorId_devuelve200() throws Exception {
        when(crudUseCase.buscarPorId(1L)).thenReturn(viaje());

        mockMvc.perform(get("/api/v1/viajes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.pasajeroId").value(10));
    }

    @Test
    void buscarPorId_inexistente_devuelve404() throws Exception {
        when(crudUseCase.buscarPorId(99L)).thenThrow(new ViajeNoEncontradoException(99L));

        mockMvc.perform(get("/api/v1/viajes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listar_devuelve200() throws Exception {
        when(crudUseCase.listar()).thenReturn(List.of(viaje()));

        mockMvc.perform(get("/api/v1/viajes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void actualizar_devuelve200() throws Exception {
        when(crudUseCase.actualizar(eq(1L), any())).thenReturn(viaje());

        mockMvc.perform(put("/api/v1/viajes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conductorId\":30,\"moneda\":\"COP\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/v1/viajes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void argumentoInvalido_devuelve400() throws Exception {
        when(crudUseCase.buscarPorId(1L)).thenThrow(new IllegalArgumentException("dato inválido"));

        mockMvc.perform(get("/api/v1/viajes/1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void eliminar_inexistente_devuelve404() throws Exception {
        org.mockito.Mockito.doThrow(new ViajeNoEncontradoException(99L))
                .when(crudUseCase).eliminar(99L);

        mockMvc.perform(delete("/api/v1/viajes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void estimar_devuelve200() throws Exception {
        when(estimarUseCase.estimar(any(), any())).thenReturn(new Estimacion(
                10.0, 20.0, new BigDecimal("13.50"), new BigDecimal("54000.00"), new BigDecimal("4000")));

        mockMvc.perform(post("/api/v1/viajes/estimar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EstimarViajeRequest(
                                4.71, -74.07, "Cra 7", 4.60, -74.08, "Aeropuerto"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.costoUSD").value(13.50))
                .andExpect(jsonPath("$.costoCOP").value(54000.00));
    }

    @Test
    void estimar_errorExterno_devuelve502() throws Exception {
        when(estimarUseCase.estimar(any(), any())).thenThrow(new ErrorExternoException("fallo externo"));

        mockMvc.perform(post("/api/v1/viajes/estimar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new EstimarViajeRequest(
                                4.71, -74.07, "Cra 7", 4.60, -74.08, "Aeropuerto"))))
                .andExpect(status().isBadGateway());
    }

    @Test
    void estimar_bodyInvalido_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/viajes/estimar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"origenLng\":-74.07,\"destinoLat\":4.60,\"destinoLng\":-74.08}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void solicitar_devuelve200() throws Exception {
        when(solicitarUseCase.solicitar(eq(10L), eq(20L), any(), any())).thenReturn(viaje());

        mockMvc.perform(post("/api/v1/viajes/solicitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new SolicitarViajeRequest(
                                10L, 20L, 4.71, -74.07, "Cra 7", 4.60, -74.08, "Aeropuerto"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ASIGNADO"));
    }

    @Test
    void solicitar_bodyInvalido_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/viajes/solicitar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"conductorId\":20}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void iniciar_devuelve200() throws Exception {
        Viaje enCurso = viaje();
        enCurso.setEstado(EstadoViaje.EN_CURSO);
        when(iniciarUseCase.iniciar(1L)).thenReturn(enCurso);

        mockMvc.perform(post("/api/v1/viajes/1/iniciar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("EN_CURSO"));
    }

    @Test
    void iniciar_estadoInvalido_devuelve409() throws Exception {
        when(iniciarUseCase.iniciar(1L)).thenThrow(new EstadoViajeInvalidoException("estado inválido"));

        mockMvc.perform(post("/api/v1/viajes/1/iniciar"))
                .andExpect(status().isConflict());
    }

    @Test
    void finalizar_devuelve200() throws Exception {
        when(finalizarUseCase.finalizar(1L)).thenReturn(viaje());

        mockMvc.perform(post("/api/v1/viajes/1/finalizar"))
                .andExpect(status().isOk());
    }

    @Test
    void cancelar_devuelve200() throws Exception {
        when(cancelarUseCase.cancelar(1L)).thenReturn(viaje());

        mockMvc.perform(post("/api/v1/viajes/1/cancelar"))
                .andExpect(status().isOk());
    }

    @Test
    void seguimiento_devuelve200() throws Exception {
        when(seguimientoUseCase.seguir(1L)).thenReturn(new Seguimiento(
                1L, EstadoViaje.EN_CURSO, 12.5, 20.0, new BigDecimal("13.50")));

        mockMvc.perform(get("/api/v1/viajes/1/seguimiento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viajeId").value(1))
                .andExpect(jsonPath("$.estado").value("EN_CURSO"))
                .andExpect(jsonPath("$.distanciaKm").value(12.5));
    }
}
