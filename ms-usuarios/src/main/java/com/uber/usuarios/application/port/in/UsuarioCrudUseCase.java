package com.uber.usuarios.application.port.in;

import com.uber.usuarios.domain.model.Usuario;

import java.util.List;

/**
 * Casos de uso CRUD base del agregado Usuario.
 */
public interface UsuarioCrudUseCase {

    Usuario registrar(Usuario usuario);

    Usuario buscarPorId(Long id);

    List<Usuario> listar();

    Usuario actualizar(Long id, Usuario datos);

    void eliminar(Long id);
}
