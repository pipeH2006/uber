package com.uber.pagos.domain.usecase;

import com.uber.pagos.domain.model.gateway.TipoCambioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarTipoCambioUseCaseTest {

    @Mock
    private TipoCambioGateway tipoCambioGateway;

    private ConsultarTipoCambioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ConsultarTipoCambioUseCase(tipoCambioGateway);
    }

    @Test
    void tipoCambio_delegaEnElGateway() {
        when(tipoCambioGateway.obtenerTipoCambio("COP")).thenReturn(new BigDecimal("4000.50"));

        BigDecimal resultado = useCase.tipoCambio("COP");

        assertThat(resultado).isEqualByComparingTo("4000.50");
    }
}
