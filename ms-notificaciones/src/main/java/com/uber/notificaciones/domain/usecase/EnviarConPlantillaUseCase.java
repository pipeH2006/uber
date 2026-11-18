package com.uber.notificaciones.domain.usecase;

import com.uber.notificaciones.domain.exception.PlantillaNoEncontradaException;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.Plantilla;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import com.uber.notificaciones.domain.model.gateway.PlantillaGateway;
import lombok.RequiredArgsConstructor;

import java.util.Map;

/**
 * Caso de uso que resuelve una plantilla, reemplaza sus marcadores y envía
 * la notificación resultante.
 */
@RequiredArgsConstructor
public class EnviarConPlantillaUseCase {

    private final PlantillaGateway plantillaGateway;
    private final EnviarNotificacionUseCase enviarNotificacion;

    public Notificacion enviarConPlantilla(String codigo, String destinatario, Map<String, String> variables) {
        Plantilla plantilla = plantillaGateway.buscarPorCodigo(codigo)
                .orElseThrow(() -> new PlantillaNoEncontradaException(codigo));
        String mensaje = plantilla.getMensaje();
        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                mensaje = mensaje.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }
        return enviarNotificacion.enviar(destinatario, mensaje, TipoNotificacion.GENERICO);
    }
}
