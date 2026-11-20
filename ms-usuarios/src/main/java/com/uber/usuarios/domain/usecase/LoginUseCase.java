package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.CredencialesInvalidasException;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@RequiredArgsConstructor
public class LoginUseCase {
    private final UsuarioGateway usuarioGateway;
    private final PasswordEncoder passwordEncoder;

    public Usuario login(String email, String contrasena) {
        Usuario usuario = usuarioGateway.buscarPorEmail(email)
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
