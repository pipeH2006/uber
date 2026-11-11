package com.uber.usuarios.infrastructure.adapter.in.rest.dto;

import com.uber.usuarios.domain.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegistroUsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120)
        String nombre,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene formato válido")
        String email,

        @Size(max = 20)
        String telefono,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
        String contrasena,

        @NotNull(message = "El rol es obligatorio")
        Rol rol
) {
}
