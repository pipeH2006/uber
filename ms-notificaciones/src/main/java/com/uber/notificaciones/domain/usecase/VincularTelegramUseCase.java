package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.model.ActualizacionTelegram;
import com.uber.notificaciones.domain.model.gateway.TelegramGateway;
import com.uber.notificaciones.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Vincula el chat de Telegram de un usuario cuando abre el bot con el enlace
 * {@code t.me/<bot>?start=<usuarioId>}: Telegram entrega el mensaje {@code /start <usuarioId>}.
 */
@RequiredArgsConstructor
public class VincularTelegramUseCase {

    private static final Pattern START = Pattern.compile("^/start\\s+(\\d{1,18})\\s*$");

    static final String MENSAJE_VINCULADO =
            "✅ ¡Listo! Tu Telegram quedó vinculado. Aquí te avisaremos de todo lo que pase con tus viajes.";
    static final String MENSAJE_USUARIO_INEXISTENTE =
            "No encontramos tu usuario. Vuelve a abrir este bot desde el botón «Vincular Telegram» de la app.";
    static final String MENSAJE_ERROR =
            "Ahora mismo no pudimos vincular tu cuenta. Intenta de nuevo en unos segundos desde la app.";

    private final TelegramGateway telegramGateway;
    private final UsuarioGateway usuarioGateway;

    /**
     * Procesa los mensajes nuevos del bot.
     *
     * @return el siguiente offset a consultar
     */
    public long procesarActualizaciones(long offset) {
        long siguiente = offset;
        for (ActualizacionTelegram actualizacion : telegramGateway.obtenerActualizaciones(offset)) {
            siguiente = Math.max(siguiente, actualizacion.updateId() + 1);
            vincularSiCorresponde(actualizacion);
        }
        return siguiente;
    }

    private void vincularSiCorresponde(ActualizacionTelegram actualizacion) {
        if (actualizacion.texto() == null || actualizacion.chatId() == null) {
            return;
        }
        Matcher matcher = START.matcher(actualizacion.texto());
        if (!matcher.matches()) {
            return;
        }
        responder(actualizacion.chatId(), vincular(Long.valueOf(matcher.group(1)), actualizacion.chatId()));
    }

    private String vincular(Long usuarioId, String chatId) {
        try {
            return usuarioGateway.vincularTelegram(usuarioId, chatId) ? MENSAJE_VINCULADO : MENSAJE_USUARIO_INEXISTENTE;
        } catch (RuntimeException e) {
            return MENSAJE_ERROR;
        }
    }

    private void responder(String chatId, String texto) {
        try {
            telegramGateway.enviar(chatId, texto);
        } catch (EnvioNotificacionException e) {
            // La vinculación ya quedó guardada; el aviso de confirmación es opcional.
        }
    }
}
