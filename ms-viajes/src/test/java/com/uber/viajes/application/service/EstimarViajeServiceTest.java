package com.uber.viajes.application.service;

import com.uber.viajes.application.port.in.Estimacion;
import com.uber.viajes.application.port.out.MapsClientPort;
import com.uber.viajes.application.port.out.TipoCambioPort;
import com.uber.viajes.domain.exception.ErrorExternoException;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.Ruta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstimarViajeServiceTest {

    @Mock
    private MapsClientPort mapsClient;

    @Mock
    private TipoCambioPort tipoCambio;

    private EstimarViajeService service;

    @BeforeEach
    void setUp() {
        service = new EstimarViajeService(mapsClient, tipoCambio);
    }

    @Test
    void estimar_calculaCostoUsdYCopCorrectamente() {
        Coordenada origen = new Coordenada(4.71, -74.07);
        Coordenada destino = new Coordenada(4.60, -74.08);
        when(mapsClient.calcularRuta(origen, destino)).thenReturn(new Ruta(10.0, 20.0));
        when(tipoCambio.obtenerTipoCambio("COP")).thenReturn(new BigDecimal("4000"));

        Estimacion estimacion = service.estimar(origen, destino);

        // 2.50 + 0.80*10 + 0.15*20 = 13.50
        assertThat(estimacion.distanciaKm()).isEqualTo(10.0);
        assertThat(estimacion.duracionMin()).isEqualTo(20.0);
        assertThat(estimacion.costoUSD()).isEqualByComparingTo("13.50");
        assertThat(estimacion.costoCOP()).isEqualByComparingTo("54000.00");
        assertThat(estimacion.tipoCambio()).isEqualByComparingTo("4000");
    }

    @Test
    void estimar_redondeaCostoUsdADosDecimales() {
        Coordenada origen = new Coordenada(4.71, -74.07);
        Coordenada destino = new Coordenada(4.60, -74.08);
        when(mapsClient.calcularRuta(origen, destino)).thenReturn(new Ruta(5.0, 7.5));
        when(tipoCambio.obtenerTipoCambio("COP")).thenReturn(new BigDecimal("4000"));

        Estimacion estimacion = service.estimar(origen, destino);

        // 2.50 + 0.80*5 + 0.15*7.5 = 2.50 + 4.00 + 1.125 = 7.625 -> 7.63
        assertThat(estimacion.costoUSD()).isEqualByComparingTo("7.63");
    }

    @Test
    void estimar_errorExterno_lanzaExcepcion() {
        Coordenada origen = new Coordenada(4.71, -74.07);
        Coordenada destino = new Coordenada(4.60, -74.08);
        when(mapsClient.calcularRuta(origen, destino)).thenThrow(new ErrorExternoException("fallo externo"));

        assertThatThrownBy(() -> service.estimar(origen, destino))
                .isInstanceOf(ErrorExternoException.class);
    }
}
