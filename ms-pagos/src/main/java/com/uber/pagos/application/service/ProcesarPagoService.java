package com.uber.pagos.application.service;

import com.uber.pagos.application.port.in.ProcesarPagoUseCase;
import com.uber.pagos.application.port.out.NotificacionPort;
import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.MetodoPago;
import com.uber.pagos.domain.model.Pago;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcesarPagoService implements ProcesarPagoUseCase {

    private final PagoRepositoryPort repository;
    private final NotificacionPort notificacionPort;

    @Override
    public Pago procesar(Long viajeId, Long usuarioId, BigDecimal monto, String moneda, MetodoPago metodoPago) {
        String monedaNormalizada = (moneda == null || moneda.isBlank()) ? "USD" : moneda;
        Pago pago = Pago.builder()
                .viajeId(viajeId)
                .usuarioId(usuarioId)
                .monto(monto)
                .moneda(monedaNormalizada)
                .metodoPago(metodoPago)
                .estado(EstadoPago.APROBADO)
                .fecha(LocalDateTime.now())
                .referencia("PAG-" + UUID.randomUUID())
                .build();
        Pago guardado = repository.guardar(pago);
        notificacionPort.notificar("usuario-" + usuarioId,
                "Pago aprobado por " + monto + " " + monedaNormalizada + " (ref: " + guardado.getReferencia() + ")");
        return guardado;
    }
}
