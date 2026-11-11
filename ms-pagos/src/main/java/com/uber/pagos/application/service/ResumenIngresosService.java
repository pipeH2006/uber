package com.uber.pagos.application.service;

import com.uber.pagos.application.port.in.ResumenIngresos;
import com.uber.pagos.application.port.in.ResumenIngresosUseCase;
import com.uber.pagos.application.port.out.PagoRepositoryPort;
import com.uber.pagos.domain.model.Pago;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumenIngresosService implements ResumenIngresosUseCase {

    private final PagoRepositoryPort repository;

    @Override
    public ResumenIngresos resumen(LocalDate fecha) {
        List<Pago> pagos = repository.listarAprobadosPorFecha(fecha);
        BigDecimal totalUSD = pagos.stream()
                .map(Pago::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new ResumenIngresos(fecha, totalUSD, pagos.size());
    }
}
