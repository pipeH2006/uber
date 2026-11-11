package com.uber.pagos.domain.exception;

public class PagoNoEncontradoException extends RuntimeException {

    public PagoNoEncontradoException(Long id) {
        super("Pago no encontrado con id: " + id);
    }
}
