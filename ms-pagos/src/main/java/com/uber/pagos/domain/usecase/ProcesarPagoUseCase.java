package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.model.EstadoPago;
import com.uber.pagos.domain.model.MetodoPago;
import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.gateway.NotificacionGateway;
import com.uber.pagos.domain.model.gateway.PagoGateway;
import com.uber.pagos.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@RequiredArgsConstructor
public class ProcesarPagoUseCase {

    private final PagoGateway pagoGateway;
    private final NotificacionGateway notificacionGateway;
    private final UsuarioGateway usuarioGateway;

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
        Pago guardado = pagoGateway.guardar(pago);
        usuarioGateway.obtenerChatIdTelegram(usuarioId).ifPresent(chatId -> notificacionGateway.notificar(chatId,
                "Pago aprobado por " + monto + " " + monedaNormalizada + " (ref: " + guardado.getReferencia() + ")"));
        return guardado;
    }
}
