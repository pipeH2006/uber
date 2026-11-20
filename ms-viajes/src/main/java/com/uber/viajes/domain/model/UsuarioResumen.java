package com.uber.viajes.domain.model;

/**
 * Lo que ms-viajes necesita saber de un usuario de ms-usuarios para decidir si puede viajar o conducir.
 */
public record UsuarioResumen(Long id, String estado, String estadoDocumento) {

    public boolean estaActivo() {
        return "ACTIVO".equals(estado);
    }

    public boolean documentoVerificado() {
        return "VERIFICADO".equals(estadoDocumento);
    }
}
