package com.uber.viajes.application.service;

import com.uber.viajes.application.port.in.Estimacion;
import com.uber.viajes.application.port.in.EstimarViajeUseCase;
import com.uber.viajes.application.port.out.NotificacionPort;
import com.uber.viajes.application.port.out.ViajeRepositoryPort;
import com.uber.viajes.domain.model.Coordenada;
import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitarViajeServiceTest {

    @Mock
    private EstimarViajeUseCase estimarUseCase;

    @Mock
    private NotificacionPort notificacion;

    @Mock
    private ViajeRepositoryPort repository;

    private SolicitarViajeService service;

    @BeforeEach
    void setUp() {
        service = new SolicitarViajeService(estimarUseCase, notificacion, repository);
    }

    @Test
    void solicitar_creaViajeAsignadoYNotificaConductor() {
        Coordenada origen = new Coordenada(4.71, -74.07);
        Coordenada destino = new Coordenada(4.60, -74.08);
        when(estimarUseCase.estimar(origen, destino)).thenReturn(new Estimacion(
                10.0, 20.0, new BigDecimal("13.50"), new BigDecimal("54000.00"), new BigDecimal("4000")));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Viaje viaje = service.solicitar(1L, 2L, origen, destino);

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

        verify(notificacion).notificar("conductor-2", "Se te ha asignado un nuevo viaje");
    }
}
