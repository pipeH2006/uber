package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.EnvioNotificacionException;
import com.uber.notificaciones.domain.model.ActualizacionTelegram;
import com.uber.notificaciones.domain.model.gateway.TelegramGateway;
import com.uber.notificaciones.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class VincularTelegramUseCase {
    private static final Pattern START_CON_ID = Pattern.compile("^/start\\s+(\\d{1,18})\\s*$");
    private static final Pattern START_SIN_ID = Pattern.compile("^/start\\s*$");

    static final String MENSAJE_VINCULADO =
            "✅ ¡Listo! Tu Telegram quedó vinculado. Aquí te avisaremos de todo lo que pase con tus viajes.";
    static final String MENSAJE_USUARIO_INEXISTENTE =
            "No encontramos tu usuario. Vuelve a abrir este bot desde el botón «Vincular Telegram» de la app.";
    static final String MENSAJE_ERROR =
            "Ahora mismo no pudimos vincular tu cuenta. Intenta de nuevo en unos segundos desde la app.";
    static final String MENSAJE_PEDIR_CONTACTO =
            "👋 ¡Hola! Para vincular tu cuenta comparte tu número con el botón de abajo. "
                    + "Debe ser el mismo teléfono con el que te registraste en la app.";
    static final String MENSAJE_CONTACTO_AJENO =
            "Ese contacto no es tu número. Pulsa /start y comparte tu propio número con el botón.";
    static final String MENSAJE_TELEFONO_SIN_USUARIO =
            "No encontramos ningún usuario registrado con ese teléfono. Regístrate en la app con este número "
                    + "y vuelve a escribir /start.";

    private final TelegramGateway telegramGateway;
    private final UsuarioGateway usuarioGateway;

    public long procesarActualizaciones(long offset) {
        long siguiente = offset;
        for (ActualizacionTelegram actualizacion : telegramGateway.obtenerActualizaciones(offset)) {
            siguiente = Math.max(siguiente, actualizacion.updateId() + 1);
            procesar(actualizacion);
        }
        return siguiente;
    }

    private void procesar(ActualizacionTelegram actualizacion) {
        String chatId = actualizacion.chatId();
        if (chatId == null) {
            return;
        }
        if (actualizacion.telefono() != null) {
            responder(chatId, actualizacion.contactoPropio()
                    ? vincularPorTelefono(actualizacion.telefono(), chatId)
                    : MENSAJE_CONTACTO_AJENO);
            return;
        }
        String texto = actualizacion.texto();
        if (texto == null) {
            return;
        }
        Matcher conId = START_CON_ID.matcher(texto);
        if (conId.matches()) {
            responder(chatId, vincularPorId(Long.valueOf(conId.group(1)), chatId));
        } else if (START_SIN_ID.matcher(texto).matches()) {
            pedirContacto(chatId);
        }
    }

    private String vincularPorId(Long usuarioId, String chatId) {
        try {
            return usuarioGateway.vincularTelegram(usuarioId, chatId) ? MENSAJE_VINCULADO : MENSAJE_USUARIO_INEXISTENTE;
        } catch (RuntimeException e) {
            return MENSAJE_ERROR;
        }
    }

    private String vincularPorTelefono(String telefono, String chatId) {
        try {
            return usuarioGateway.vincularPorTelefono(telefono, chatId) > 0 ? MENSAJE_VINCULADO : MENSAJE_TELEFONO_SIN_USUARIO;
        } catch (RuntimeException e) {
            return MENSAJE_ERROR;
        }
    }

    private void pedirContacto(String chatId) {
        try {
            telegramGateway.solicitarContacto(chatId, MENSAJE_PEDIR_CONTACTO);
        } catch (EnvioNotificacionException e) {
        }
    }

    private void responder(String chatId, String texto) {
        try {
            telegramGateway.enviar(chatId, texto);
        } catch (EnvioNotificacionException e) {
        }
    }
}
