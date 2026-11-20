package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DesactivarUsuarioUseCase {
    private final UsuarioGateway usuarioGateway;

    public Usuario desactivar(Long id) {
        Usuario usuario = usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));

        if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
            usuario.setEstado(EstadoUsuario.INACTIVO);
            return usuarioGateway.guardar(usuario);
        }
        throw new EstadoInvalidoException("No se puede desactivar un usuario en estado " + usuario.getEstado());
    }
}
