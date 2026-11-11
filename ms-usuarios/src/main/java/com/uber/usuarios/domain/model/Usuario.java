package com.uber.usuarios.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Agregado raíz del subdominio de usuarios. Representa tanto a un pasajero
 * como a un conductor (diferenciados por {@link Rol}).
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String contrasenaHash;
    private Rol rol;
    private EstadoUsuario estado;
    private EstadoDocumento estadoDocumento;
    private LocalDateTime fechaRegistro;

    public boolean esConductor() {
        return rol == Rol.CONDUCTOR;
    }

    public boolean estaActivo() {
        return estado == EstadoUsuario.ACTIVO;
    }
}
