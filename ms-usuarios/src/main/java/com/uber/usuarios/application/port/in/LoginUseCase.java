package com.uber.usuarios.application.port.in;

import com.uber.usuarios.domain.model.Usuario;

public interface LoginUseCase {

    Usuario login(String email, String contrasena);
}
