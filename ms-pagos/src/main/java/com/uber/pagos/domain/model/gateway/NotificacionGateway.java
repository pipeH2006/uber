package com.uber.pagos.domain.model.gateway;

public interface NotificacionGateway {
    void notificar(String destinatario, String mensaje);
}
