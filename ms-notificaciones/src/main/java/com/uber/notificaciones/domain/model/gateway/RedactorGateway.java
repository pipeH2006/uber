package com.uber.notificaciones.domain.model.gateway;

/**
 * Puerto de salida hacia el servicio de IA que redacta los mensajes al usuario.
 */
public interface RedactorGateway {

    /**
     * Reescribe el mensaje con un tono cálido y breve sin alterar los datos. Nunca falla:
     * si la IA no está disponible devuelve el mensaje original.
     */
    String redactar(String mensajeBase);
}
