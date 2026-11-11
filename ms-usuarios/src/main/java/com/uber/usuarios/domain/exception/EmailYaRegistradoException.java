package com.uber.usuarios.domain.exception;

public class EmailYaRegistradoException extends RuntimeException {

    public EmailYaRegistradoException(String email) {
        super("El email ya se encuentra registrado: " + email);
    }
}
