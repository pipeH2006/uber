package com.uber.usuarios.application.port.out;

import com.uber.usuarios.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (driven port) hacia la persistencia.
 */
public interface UsuarioRepositoryPort {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    List<Usuario> listar();

    void eliminar(Long id);

    boolean existePorEmail(String email);
}
