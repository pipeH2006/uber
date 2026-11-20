package com.uber.usuarios.infraestructure.mapper;

import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.infraestructure.driver_adapters.jpa_repository.UsuarioData;
import org.springframework.stereotype.Component;

@Component
public class MapperUsuario {
    public Usuario toUsuario(UsuarioData data) {
        return Usuario.builder()
                .id(data.getId())
                .nombre(data.getNombre())
                .email(data.getEmail())
                .telefono(data.getTelefono())
                .telegramChatId(data.getTelegramChatId())
                .contrasenaHash(data.getContrasenaHash())
                .rol(data.getRol())
                .estado(data.getEstado())
                .estadoDocumento(data.getEstadoDocumento())
                .fechaRegistro(data.getFechaRegistro())
                .build();
    }

    public UsuarioData toData(Usuario usuario) {
        return UsuarioData.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .telefono(usuario.getTelefono())
                .telegramChatId(usuario.getTelegramChatId())
                .contrasenaHash(usuario.getContrasenaHash())
                .rol(usuario.getRol())
                .estado(usuario.getEstado())
                .estadoDocumento(usuario.getEstadoDocumento())
                .fechaRegistro(usuario.getFechaRegistro())
                .build();
    }
}
