package com.uber.notificaciones.infraestructure.entry_points;

import com.uber.notificaciones.domain.usecase.VincularTelegramUseCase;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TelegramUpdatesPollerTest {
    @Mock
    private VincularTelegramUseCase vincularTelegramUseCase;

    @Test
    void sondear_avanzaElOffsetEntreConsultas() {
        TelegramUpdatesPoller poller = new TelegramUpdatesPoller(vincularTelegramUseCase);
        when(vincularTelegramUseCase.procesarActualizaciones(0)).thenReturn(5L);
        when(vincularTelegramUseCase.procesarActualizaciones(5)).thenReturn(5L);

        poller.sondear();
        poller.sondear();

        verify(vincularTelegramUseCase).procesarActualizaciones(0);
        verify(vincularTelegramUseCase).procesarActualizaciones(5);
    }

    @Test
    void sondear_siFallaElProceso_noPropagaLaExcepcion() {
        TelegramUpdatesPoller poller = new TelegramUpdatesPoller(vincularTelegramUseCase);
        when(vincularTelegramUseCase.procesarActualizaciones(0)).thenThrow(new IllegalStateException("fallo"));

        assertThatCode(poller::sondear).doesNotThrowAnyException();
    }
}
