package com.uber.usuarios.domain.model.gateway;

import com.uber.usuarios.domain.model.Usuario;

import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida (gateway) hacia la persistencia de usuarios.
 */
public interface UsuarioGateway {

    Usuario guardar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    Optional<Usuario> buscarPorEmail(String email);

    List<Usuario> listar();

    void eliminar(Long id);

    boolean existePorEmail(String email);
}
