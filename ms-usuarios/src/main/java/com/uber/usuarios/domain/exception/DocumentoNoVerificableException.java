package com.uber.usuarios.domain.exception;

public class DocumentoNoVerificableException extends RuntimeException {

    public DocumentoNoVerificableException(Long id) {
        super("El usuario " + id + " no es conductor, no posee documento verificable");
    }
}
