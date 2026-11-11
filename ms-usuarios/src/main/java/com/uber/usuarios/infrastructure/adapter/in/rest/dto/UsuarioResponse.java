package com.uber.usuarios.infrastructure.adapter.in.rest.dto;

import com.uber.usuarios.domain.model.EstadoDocumento;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Rol;
import com.uber.usuarios.domain.model.Usuario;

import java.time.LocalDateTime;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        String telefono,
        Rol rol,
        EstadoUsuario estado,
        EstadoDocumento estadoDocumento,
        LocalDateTime fechaRegistro
) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(
                u.getId(),
                u.getNombre(),
                u.getEmail(),
                u.getTelefono(),
                u.getRol(),
                u.getEstado(),
                u.getEstadoDocumento(),
                u.getFechaRegistro()
        );
    }
}
