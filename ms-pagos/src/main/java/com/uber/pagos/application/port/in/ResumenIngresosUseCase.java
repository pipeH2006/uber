package com.uber.pagos.application.port.in;

import java.time.LocalDate;

public interface ResumenIngresosUseCase {

    ResumenIngresos resumen(LocalDate fecha);
}
