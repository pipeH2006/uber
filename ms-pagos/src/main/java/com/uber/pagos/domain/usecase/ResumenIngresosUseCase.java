package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.model.Pago;
import com.uber.pagos.domain.model.ResumenIngresos;
import com.uber.pagos.domain.model.gateway.PagoGateway;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class ResumenIngresosUseCase {
    private final PagoGateway pagoGateway;

    public ResumenIngresos resumen(LocalDate fecha) {
        List<Pago> pagos = pagoGateway.listarAprobadosPorFecha(fecha);
        BigDecimal totalUSD = pagos.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ResumenIngresos(fecha, totalUSD, pagos.size());
    }
}
