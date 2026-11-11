package com.uber.pagos.application.service;

import com.uber.pagos.application.port.out.TipoCambioPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarTipoCambioServiceTest {

    @Mock
    private TipoCambioPort tipoCambioPort;

    private ConsultarTipoCambioService service;

    @BeforeEach
    void setUp() {
        service = new ConsultarTipoCambioService(tipoCambioPort);
    }

    @Test
    void tipoCambio_delegaEnElPuerto() {
        when(tipoCambioPort.obtenerTipoCambio("COP")).thenReturn(new BigDecimal("4000.50"));

        BigDecimal resultado = service.tipoCambio("COP");

        assertThat(resultado).isEqualByComparingTo("4000.50");
    }
}
