package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.in.EnviarConPlantillaUseCase;
import com.uber.notificaciones.application.port.in.EnviarNotificacionUseCase;
import com.uber.notificaciones.application.port.out.PlantillaRepositoryPort;
import com.uber.notificaciones.domain.exception.PlantillaNoEncontradaException;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.Plantilla;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnviarConPlantillaService implements EnviarConPlantillaUseCase {

    private final PlantillaRepositoryPort plantillaRepository;
    private final EnviarNotificacionUseCase enviarNotificacion;

    @Override
    public Notificacion enviarConPlantilla(String codigo, String destinatario, Map<String, String> variables) {
        Plantilla plantilla = plantillaRepository.buscarPorCodigo(codigo)
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
