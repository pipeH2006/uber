package com.uber.pagos.application.port.out;

public interface NotificacionPort {

    void notificar(String destinatario, String mensaje);
}
