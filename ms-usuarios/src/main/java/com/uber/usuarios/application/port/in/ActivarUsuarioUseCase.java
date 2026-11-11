package com.uber.usuarios.application.port.in;

import com.uber.usuarios.domain.model.Usuario;

public interface ActivarUsuarioUseCase {

    Usuario activar(Long id);
}
