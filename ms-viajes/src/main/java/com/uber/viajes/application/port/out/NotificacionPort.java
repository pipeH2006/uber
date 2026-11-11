package com.uber.viajes.application.port.out;

public interface NotificacionPort {

    void notificar(String destinatario, String mensaje);
}
