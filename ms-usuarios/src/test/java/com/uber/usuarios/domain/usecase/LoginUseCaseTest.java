package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.CredencialesInvalidasException;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {
    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private PasswordEncoder passwordEncoder;

    private LoginUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginUseCase(usuarioGateway, passwordEncoder);
    }

    @Test
    void login_credencialesCorrectas_devuelveUsuario() {
        Usuario u = Usuario.builder().id(1L).email("ana@correo.com")
                .contrasenaHash("hash").estado(EstadoUsuario.ACTIVO).build();
        when(usuarioGateway.buscarPorEmail("ana@correo.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("secreto", "hash")).thenReturn(true);

        assertThat(useCase.login("ana@correo.com", "secreto")).isEqualTo(u);
    }

    @Test
    void login_contrasenaIncorrecta_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(1L).email("ana@correo.com")
                .contrasenaHash("hash").estado(EstadoUsuario.ACTIVO).build();
        when(usuarioGateway.buscarPorEmail("ana@correo.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        assertThatThrownBy(() -> useCase.login("ana@correo.com", "mala"))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void login_emailInexistente_lanzaExcepcion() {
        when(usuarioGateway.buscarPorEmail("nadie@correo.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.login("nadie@correo.com", "secreto"))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void login_usuarioInactivo_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(1L).email("ana@correo.com")
                .contrasenaHash("hash").estado(EstadoUsuario.INACTIVO).build();
        when(usuarioGateway.buscarPorEmail("ana@correo.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("secreto", "hash")).thenReturn(true);

        assertThatThrownBy(() -> useCase.login("ana@correo.com", "secreto"))
                .isInstanceOf(EstadoInvalidoException.class);
    }
}
