package com.uber.notificaciones.application.port.in;

import com.uber.notificaciones.domain.model.Notificacion;

import java.util.Map;

/**
 * Caso de uso que resuelve una plantilla, reemplaza sus marcadores y envía
 * la notificación resultante.
 */
public interface EnviarConPlantillaUseCase {

    Notificacion enviarConPlantilla(String codigo, String destinatario, Map<String, String> variables);
}
