package com.uber.viajes.domain.usecase;

import com.uber.viajes.domain.exception.UsuarioNoEncontradoException;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Estimacion;
import com.uber.viajes.domain.model.Viaje;
import com.uber.viajes.domain.model.gateway.NotificacionGateway;
import com.uber.viajes.domain.model.gateway.UsuarioGateway;
import com.uber.viajes.domain.model.gateway.ViajeGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitarViajeUseCaseTest {

    @Mock
    private EstimarViajeUseCase estimarViajeUseCase;

    @Mock
    private NotificacionGateway notificacionGateway;

    @Mock
    private ViajeGateway viajeGateway;

    @Mock
    private UsuarioGateway usuarioGateway;

    private SolicitarViajeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SolicitarViajeUseCase(estimarViajeUseCase, notificacionGateway, viajeGateway, usuarioGateway);
        lenient().when(usuarioGateway.usuarioExiste(any())).thenReturn(true);
    }

    @Test
    void solicitar_creaViajeAsignadoYNotificaConductor() {
        Coordenada origen = new Coordenada(4.71, -74.07);
        Coordenada destino = new Coordenada(4.60, -74.08);
        when(estimarViajeUseCase.estimar(origen, destino)).thenReturn(new Estimacion(
                10.0, 20.0, new BigDecimal("13.50"), new BigDecimal("54000.00"), new BigDecimal("4000")));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioGateway.obtenerChatIdTelegram(2L)).thenReturn(Optional.of("555"));

        Viaje viaje = useCase.solicitar(1L, 2L, origen, destino);

        assertThat(viaje.getPasajeroId()).isEqualTo(1L);
        assertThat(viaje.getConductorId()).isEqualTo(2L);
        assertThat(viaje.getEstado()).isEqualTo(EstadoViaje.ASIGNADO);
        assertThat(viaje.getOrigenLat()).isEqualTo(4.71);
        assertThat(viaje.getDestinoLng()).isEqualTo(-74.08);
        assertThat(viaje.getDistanciaKm()).isEqualTo(10.0);
        assertThat(viaje.getDuracionMin()).isEqualTo(20.0);
        assertThat(viaje.getCostoEstimado()).isEqualByComparingTo("13.50");
        assertThat(viaje.getMoneda()).isEqualTo("USD");
        assertThat(viaje.getFechaSolicitud()).isNotNull();

        verify(notificacionGateway).notificar("555", "Se te ha asignado un nuevo viaje: 10.0 km · 20 min · 13.50 USD");
    }

    @Test
    void solicitar_conductorSinChat_creaViajeSinNotificar() {
        Coordenada origen = new Coordenada(4.71, -74.07);
        Coordenada destino = new Coordenada(4.60, -74.08);
        when(estimarViajeUseCase.estimar(origen, destino)).thenReturn(new Estimacion(
                10.0, 20.0, new BigDecimal("13.50"), new BigDecimal("54000.00"), new BigDecimal("4000")));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));
        when(usuarioGateway.obtenerChatIdTelegram(2L)).thenReturn(Optional.empty());

        Viaje viaje = useCase.solicitar(1L, 2L, origen, destino);

        assertThat(viaje.getEstado()).isEqualTo(EstadoViaje.ASIGNADO);
        verifyNoInteractions(notificacionGateway);
    }

    @Test
    void solicitar_pasajeroInexistente_lanzaExcepcionSinCrearViaje() {
        when(usuarioGateway.usuarioExiste(1L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.solicitar(1L, 2L, new Coordenada(4.71, -74.07), new Coordenada(4.60, -74.08)))
                .isInstanceOf(UsuarioNoEncontradoException.class)
                .hasMessageContaining("1");

        verifyNoInteractions(estimarViajeUseCase, viajeGateway, notificacionGateway);
    }

    @Test
    void solicitar_conductorInexistente_lanzaExcepcionSinCrearViaje() {
        when(usuarioGateway.usuarioExiste(2L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.solicitar(1L, 2L, new Coordenada(4.71, -74.07), new Coordenada(4.60, -74.08)))
                .isInstanceOf(UsuarioNoEncontradoException.class)
                .hasMessageContaining("2");

        verifyNoInteractions(estimarViajeUseCase, viajeGateway, notificacionGateway);
    }

    @Test
    void solicitar_pasajeroConChat_tambienLoNotificaQueElViajeFueAsignado() {
        Coordenada origen = new Coordenada(4.71, -74.07);
        Coordenada destino = new Coordenada(4.60, -74.08);
        when(estimarViajeUseCase.estimar(origen, destino)).thenReturn(new Estimacion(
                10.0, 20.0, new BigDecimal("13.50"), new BigDecimal("54000.00"), new BigDecimal("4000")));
        when(viajeGateway.guardar(any())).thenAnswer(inv -> {
            Viaje v = inv.getArgument(0);
            v.setId(5L);
            return v;
        });
        when(usuarioGateway.obtenerChatIdTelegram(1L)).thenReturn(Optional.of("111"));
        when(usuarioGateway.obtenerChatIdTelegram(2L)).thenReturn(Optional.empty());

        useCase.solicitar(1L, 2L, origen, destino);

        verify(notificacionGateway).notificar("111", "Tu viaje #5 fue asignado a un conductor y va en camino");
    }
}
