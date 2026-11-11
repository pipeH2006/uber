package com.uber.pagos.domain.exception;

public class EstadoPagoInvalidoException extends RuntimeException {

    public EstadoPagoInvalidoException(String message) {
        super(message);
    }
}
