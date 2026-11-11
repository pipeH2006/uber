package com.uber.usuarios.application.service;

import com.uber.usuarios.application.port.in.DesactivarUsuarioUseCase;
import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DesactivarUsuarioService implements DesactivarUsuarioUseCase {

    private final UsuarioRepositoryPort repository;

    @Override
    public Usuario desactivar(Long id) {
        Usuario usuario = repository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));

        if (usuario.getEstado() == EstadoUsuario.ACTIVO) {
            usuario.setEstado(EstadoUsuario.INACTIVO);
            return repository.guardar(usuario);
        }
        throw new EstadoInvalidoException("No se puede desactivar un usuario en estado " + usuario.getEstado());
    }
}
