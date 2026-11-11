package com.uber.usuarios.infrastructure.adapter.in.rest.dto;

import com.uber.usuarios.domain.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record ActualizarUsuarioRequest(
        @Size(max = 120)
        String nombre,

        @Email(message = "El email no tiene formato válido")
        String email,

        @Size(max = 20)
        String telefono,

        Rol rol
) {
}
