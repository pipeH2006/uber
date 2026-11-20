package com.uber.notificaciones.infraestructure.entry_points;

import com.uber.notificaciones.domain.usecase.VincularTelegramUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TelegramUpdatesPoller {
    private final VincularTelegramUseCase vincularTelegramUseCase;

    private long offset = 0;

    @Scheduled(fixedDelayString = "${telegram.polling.intervalo-ms:3000}")
    public void sondear() {
        try {
            offset = vincularTelegramUseCase.procesarActualizaciones(offset);
        } catch (RuntimeException e) {
            log.warn("Error al procesar los mensajes del bot: {}", e.getMessage());
        }
    }
}
