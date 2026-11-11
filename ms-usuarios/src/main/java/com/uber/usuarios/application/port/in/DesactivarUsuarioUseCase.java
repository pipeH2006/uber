package com.uber.usuarios.application.port.in;

import com.uber.usuarios.domain.model.Usuario;

public interface DesactivarUsuarioUseCase {

    Usuario desactivar(Long id);
}
