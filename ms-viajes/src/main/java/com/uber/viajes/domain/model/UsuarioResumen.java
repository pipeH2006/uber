package com.uber.viajes.domain.model;

public record UsuarioResumen(Long id, String estado, String estadoDocumento) {
    public boolean estaActivo() {
        return "ACTIVO".equals(estado);
    }

    public boolean documentoVerificado() {
        return "VERIFICADO".equals(estadoDocumento);
    }
}
