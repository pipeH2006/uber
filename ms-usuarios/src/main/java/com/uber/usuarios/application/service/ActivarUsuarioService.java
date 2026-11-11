package com.uber.usuarios.application.service;

import com.uber.usuarios.application.port.in.ActivarUsuarioUseCase;
import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivarUsuarioService implements ActivarUsuarioUseCase {

    private final UsuarioRepositoryPort repository;

    @Override
    public Usuario activar(Long id) {
        Usuario usuario = repository.buscarPorId(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));

        if (usuario.getEstado() == EstadoUsuario.PENDIENTE || usuario.getEstado() == EstadoUsuario.INACTIVO) {
            usuario.setEstado(EstadoUsuario.ACTIVO);
            return repository.guardar(usuario);
        }
        throw new EstadoInvalidoException("No se puede activar un usuario en estado " + usuario.getEstado());
    }
}
