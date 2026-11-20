package com.uber.usuarios.infraestructure.entry_points.dto;

import com.uber.usuarios.domain.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ActualizarUsuarioRequest(
        @Size(max = 120)
        String nombre,

        @Email(message = "El email no tiene formato válido")
        String email,

        @Size(max = 20)
        String telefono,

        Rol rol,

        @Pattern(regexp = "^-?\\d{1,20}$", message = "El chat ID de Telegram debe ser numérico")
        String telegramChatId
) {
}
