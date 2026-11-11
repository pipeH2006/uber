package com.uber.pagos.infrastructure.adapter.in.rest;

import com.uber.pagos.application.port.in.ConsultarTipoCambioUseCase;
import com.uber.pagos.application.port.in.EvaluarFraudeUseCase;
import com.uber.pagos.application.port.in.Factura;
import com.uber.pagos.application.port.in.GenerarFacturaUseCase;
import com.uber.pagos.application.port.in.PagoCrudUseCase;
import com.uber.pagos.application.port.in.ProcesarPagoUseCase;
import com.uber.pagos.application.port.in.ReembolsarPagoUseCase;
import com.uber.pagos.application.port.in.ResumenIngresos;
import com.uber.pagos.application.port.in.ResumenIngresosUseCase;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResultadoFraude;
import com.uber.pagos.infrastructure.adapter.in.rest.dto.ActualizarPagoRequest;
import com.uber.pagos.infrastructure.adapter.in.rest.dto.PagoRequest;
import com.uber.pagos.infrastructure.adapter.in.rest.dto.PagoResponse;
import com.uber.pagos.infrastructure.adapter.in.rest.dto.ProcesarPagoRequest;
import com.uber.pagos.infrastructure.adapter.in.rest.dto.TipoCambioResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoCrudUseCase crudUseCase;
    private final ProcesarPagoUseCase procesarUseCase;
    private final ReembolsarPagoUseCase reembolsarUseCase;
    private final GenerarFacturaUseCase facturaUseCase;
    private final EvaluarFraudeUseCase fraudeUseCase;
    private final ConsultarTipoCambioUseCase tipoCambioUseCase;
    private final ResumenIngresosUseCase resumenUseCase;

    // ---- CRUD base ----

    @PostMapping
    public ResponseEntity<PagoResponse> crear(@Valid @RequestBody PagoRequest request) {
        Pago pago = crudUseCase.crear(toDomain(request));
        return ResponseEntity.created(URI.create("/api/v1/pagos/" + pago.getId()))
                .body(PagoResponse.from(pago));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PagoResponse> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(PagoResponse.from(crudUseCase.buscarPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<PagoResponse>> listar() {
        List<PagoResponse> pagos = crudUseCase.listar().stream()
                .map(PagoResponse::from)
                .toList();
        return ResponseEntity.ok(pagos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PagoResponse> actualizar(@PathVariable Long id,
                                                   @Valid @RequestBody ActualizarPagoRequest request) {
        Pago datos = Pago.builder()
                .monto(request.monto())
                .moneda(request.moneda())
                .metodoPago(request.metodoPago())
                .estado(request.estado())
                .build();
        return ResponseEntity.ok(PagoResponse.from(crudUseCase.actualizar(id, datos)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        crudUseCase.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // ---- APIs de dominio (no CRUD) ----

    @PostMapping("/procesar")
    public ResponseEntity<PagoResponse> procesar(@Valid @RequestBody ProcesarPagoRequest request) {
        Pago pago = procesarUseCase.procesar(
                request.viajeId(), request.usuarioId(), request.monto(), request.moneda(), request.metodoPago());
        return ResponseEntity.ok(PagoResponse.from(pago));
    }

    @PostMapping("/{id}/reembolsar")
    public ResponseEntity<PagoResponse> reembolsar(@PathVariable Long id) {
        return ResponseEntity.ok(PagoResponse.from(reembolsarUseCase.reembolsar(id)));
    }

    @GetMapping("/{id}/factura")
    public ResponseEntity<Factura> factura(@PathVariable Long id) {
        return ResponseEntity.ok(facturaUseCase.generarFactura(id));
    }

    @PostMapping("/{id}/evaluar-fraude")
    public ResponseEntity<ResultadoFraude> evaluarFraude(@PathVariable Long id) {
        return ResponseEntity.ok(fraudeUseCase.evaluarFraude(id));
    }

    @GetMapping("/tipo-cambio")
    public ResponseEntity<TipoCambioResponse> tipoCambio(@RequestParam String moneda) {
        BigDecimal tc = tipoCambioUseCase.tipoCambio(moneda);
        return ResponseEntity.ok(new TipoCambioResponse(moneda, tc));
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenIngresos> resumen(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(resumenUseCase.resumen(fecha));
    }

    private Pago toDomain(PagoRequest r) {
        return Pago.builder()
                .viajeId(r.viajeId())
                .usuarioId(r.usuarioId())
                .monto(r.monto())
                .moneda(r.moneda())
                .metodoPago(r.metodoPago())
                .estado(r.estado())
                .build();
    }
}
