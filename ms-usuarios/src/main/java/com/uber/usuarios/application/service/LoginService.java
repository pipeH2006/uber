package com.uber.usuarios.application.service;

import com.uber.usuarios.application.port.in.LoginUseCase;
import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.exception.CredencialesInvalidasException;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.model.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final UsuarioRepositoryPort repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Usuario login(String email, String contrasena) {
        Usuario usuario = repository.buscarPorEmail(email)
                .orElseThrow(CredencialesInvalidasException::new);

        if (!passwordEncoder.matches(contrasena, usuario.getContrasenaHash())) {
            throw new CredencialesInvalidasException();
        }

        if (!usuario.estaActivo()) {
            throw new EstadoInvalidoException("El usuario no se encuentra activo");
        }

        return usuario;
    }
}
