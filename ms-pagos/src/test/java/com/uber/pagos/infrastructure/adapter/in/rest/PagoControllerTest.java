package com.uber.pagos.infrastructure.adapter.in.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uber.pagos.application.port.in.ConsultarTipoCambioUseCase;
import com.uber.pagos.application.port.in.EvaluarFraudeUseCase;
import com.uber.pagos.application.port.in.Factura;
import com.uber.pagos.application.port.in.GenerarFacturaUseCase;
import com.uber.pagos.application.port.in.PagoCrudUseCase;
import com.uber.pagos.application.port.in.ProcesarPagoUseCase;
import com.uber.pagos.application.port.in.ReembolsarPagoUseCase;
import com.uber.pagos.application.port.in.ResumenIngresos;
import com.uber.pagos.application.port.in.ResumenIngresosUseCase;
import com.uber.pagos.domain.exception.ErrorExternoException;
import com.uber.pagos.domain.exception.EstadoPagoInvalidoException;
import com.uber.pagos.domain.exception.PagoNoEncontradoException;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.MetodoPago;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResultadoFraude;
import com.uber.pagos.infrastructure.adapter.in.rest.dto.PagoRequest;
import com.uber.pagos.infrastructure.adapter.in.rest.dto.ProcesarPagoRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PagoCrudUseCase crudUseCase;

    @MockBean
    private ProcesarPagoUseCase procesarUseCase;

    @MockBean
    private ReembolsarPagoUseCase reembolsarUseCase;

    @MockBean
    private GenerarFacturaUseCase facturaUseCase;

    @MockBean
    private EvaluarFraudeUseCase fraudeUseCase;

    @MockBean
    private ConsultarTipoCambioUseCase tipoCambioUseCase;

    @MockBean
    private ResumenIngresosUseCase resumenUseCase;

    private Pago pago() {
        return Pago.builder()
                .id(1L)
                .viajeId(10L)
                .usuarioId(2L)
                .monto(new BigDecimal("100.50"))
                .moneda("USD")
                .metodoPago(MetodoPago.TARJETA)
                .estado(EstadoPago.APROBADO)
                .fecha(LocalDateTime.now())
                .referencia("PAG-123")
                .riesgoFraude("BAJO")
                .build();
    }

    @Test
    void crear_devuelve201() throws Exception {
        when(crudUseCase.crear(any())).thenReturn(pago());

        mockMvc.perform(post("/api/v1/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new PagoRequest(
                                10L, 2L, new BigDecimal("100.50"), "USD", MetodoPago.TARJETA, EstadoPago.PENDIENTE))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    @Test
    void crear_bodyInvalido_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"viajeId\":10,\"usuarioId\":2,\"monto\":-5,\"metodoPago\":null}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void buscarPorId_devuelve200() throws Exception {
        when(crudUseCase.buscarPorId(1L)).thenReturn(pago());

        mockMvc.perform(get("/api/v1/pagos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.moneda").value("USD"))
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    @Test
    void buscarPorId_inexistente_devuelve404() throws Exception {
        when(crudUseCase.buscarPorId(99L)).thenThrow(new PagoNoEncontradoException(99L));

        mockMvc.perform(get("/api/v1/pagos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void listar_devuelve200() throws Exception {
        when(crudUseCase.listar()).thenReturn(List.of(pago()));

        mockMvc.perform(get("/api/v1/pagos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void actualizar_devuelve200() throws Exception {
        when(crudUseCase.actualizar(eq(1L), any())).thenReturn(pago());

        mockMvc.perform(put("/api/v1/pagos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"monto\":200.00,\"moneda\":\"COP\",\"metodoPago\":\"EFECTIVO\",\"estado\":\"APROBADO\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void eliminar_devuelve204() throws Exception {
        mockMvc.perform(delete("/api/v1/pagos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void procesar_devuelve200() throws Exception {
        when(procesarUseCase.procesar(eq(10L), eq(2L), any(), eq("USD"), eq(MetodoPago.TARJETA)))
                .thenReturn(pago());

        mockMvc.perform(post("/api/v1/pagos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ProcesarPagoRequest(
                                10L, 2L, new BigDecimal("100.50"), "USD", MetodoPago.TARJETA))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.estado").value("APROBADO"));
    }

    @Test
    void procesar_bodyInvalido_devuelve400() throws Exception {
        mockMvc.perform(post("/api/v1/pagos/procesar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"viajeId\":10,\"usuarioId\":2,\"monto\":null,\"metodoPago\":\"TARJETA\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void reembolsar_devuelve200() throws Exception {
        when(reembolsarUseCase.reembolsar(1L)).thenReturn(pago());

        mockMvc.perform(post("/api/v1/pagos/1/reembolsar"))
                .andExpect(status().isOk());
    }

    @Test
    void reembolsar_estadoInvalido_devuelve409() throws Exception {
        when(reembolsarUseCase.reembolsar(1L))
                .thenThrow(new EstadoPagoInvalidoException("No se puede reembolsar"));

        mockMvc.perform(post("/api/v1/pagos/1/reembolsar"))
                .andExpect(status().isConflict());
    }

    @Test
    void factura_devuelve200() throws Exception {
        when(facturaUseCase.generarFactura(1L)).thenReturn(new Factura(
                1L, 10L, new BigDecimal("100.00"), "USD", new BigDecimal("400000.00"),
                new BigDecimal("4000.00"), EstadoPago.APROBADO, "PAG-123"));

        mockMvc.perform(get("/api/v1/pagos/1/factura"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pagoId").value(1))
                .andExpect(jsonPath("$.moneda").value("USD"))
                .andExpect(jsonPath("$.estado").value("APROBADO"))
                .andExpect(jsonPath("$.montoCOP").isNumber());
    }

    @Test
    void factura_errorExterno_devuelve502() throws Exception {
        when(facturaUseCase.generarFactura(1L))
                .thenThrow(new ErrorExternoException("Servicio no disponible"));

        mockMvc.perform(get("/api/v1/pagos/1/factura"))
                .andExpect(status().isBadGateway());
    }

    @Test
    void evaluarFraude_devuelve200() throws Exception {
        when(fraudeUseCase.evaluarFraude(1L)).thenReturn(new ResultadoFraude("ALTO", 85, "Monto atípico"));

        mockMvc.perform(post("/api/v1/pagos/1/evaluar-fraude"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riesgo").value("ALTO"))
                .andExpect(jsonPath("$.score").value(85));
    }

    @Test
    void evaluarFraude_inexistente_devuelve404() throws Exception {
        when(fraudeUseCase.evaluarFraude(99L)).thenThrow(new PagoNoEncontradoException(99L));

        mockMvc.perform(post("/api/v1/pagos/99/evaluar-fraude"))
                .andExpect(status().isNotFound());
    }

    @Test
    void tipoCambio_devuelve200() throws Exception {
        when(tipoCambioUseCase.tipoCambio("COP")).thenReturn(new BigDecimal("4000.00"));

        mockMvc.perform(get("/api/v1/pagos/tipo-cambio").param("moneda", "COP"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.moneda").value("COP"))
                .andExpect(jsonPath("$.tipoCambio").value(4000.0));
    }

    @Test
    void tipoCambio_argumentoInvalido_devuelve400() throws Exception {
        when(tipoCambioUseCase.tipoCambio(anyString()))
                .thenThrow(new IllegalArgumentException("Moneda inválida"));

        mockMvc.perform(get("/api/v1/pagos/tipo-cambio").param("moneda", "XXX"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void resumen_devuelve200() throws Exception {
        when(resumenUseCase.resumen(LocalDate.of(2026, 11, 11)))
                .thenReturn(new ResumenIngresos(LocalDate.of(2026, 11, 11), new BigDecimal("150.50"), 2));

        mockMvc.perform(get("/api/v1/pagos/resumen").param("fecha", "2026-11-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fecha").value("2026-11-11"))
                .andExpect(jsonPath("$.cantidadPagos").value(2));
    }
}
