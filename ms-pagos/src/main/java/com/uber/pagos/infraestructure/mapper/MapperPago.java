package com.uber.pagos.infraestructure.mapper;

import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.infraestructure.driver_adapters.jpa_repository.PagoData;
import org.springframework.stereotype.Component;

@Component
public class MapperPago {

    public Pago toPago(PagoData data) {
        return Pago.builder()
                .id(data.getId())
                .viajeId(data.getViajeId())
                .usuarioId(data.getUsuarioId())
                .monto(data.getMonto())
                .moneda(data.getMoneda())
                .metodoPago(data.getMetodoPago())
                .estado(data.getEstado())
                .fecha(data.getFecha())
                .referencia(data.getReferencia())
                .riesgoFraude(data.getRiesgoFraude())
                .build();
    }

    public PagoData toData(Pago pago) {
        return PagoData.builder()
                .id(pago.getId())
                .viajeId(pago.getViajeId())
                .usuarioId(pago.getUsuarioId())
                .monto(pago.getMonto())
                .moneda(pago.getMoneda())
                .metodoPago(pago.getMetodoPago())
                .estado(pago.getEstado())
                .fecha(pago.getFecha())
                .referencia(pago.getReferencia())
                .riesgoFraude(pago.getRiesgoFraude())
                .build();
    }
}
