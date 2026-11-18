package com.uber.viajes.domain.model.gateway;

public interface NotificacionGateway {

    void notificar(String destinatario, String mensaje);
}
