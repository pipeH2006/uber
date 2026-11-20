package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SuspenderUsuarioUseCase {
    private final UsuarioGateway usuarioGateway;

    public Usuario suspender(Long id) {
        Usuario usuario = usuarioGateway.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
        if (usuario.getEstado() == EstadoUsuario.SUSPENDIDO) {
            throw new EstadoInvalidoException("El usuario ya se encuentra suspendido");
        }
        usuario.setEstado(EstadoUsuario.SUSPENDIDO);
        return usuarioGateway.guardar(usuario);
    }
}
