package com.uber.usuarios.application.service;

import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.exception.CredencialesInvalidasException;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Usuario;
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
class LoginServiceTest {

    @Mock
    private UsuarioRepositoryPort repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private LoginService service;

    @BeforeEach
    void setUp() {
        service = new LoginService(repository, passwordEncoder);
    }

    @Test
    void login_credencialesCorrectas_devuelveUsuario() {
        Usuario u = Usuario.builder()
                .id(1L)
                .email("ana@correo.com")
                .contrasenaHash("hash")
                .estado(EstadoUsuario.ACTIVO)
                .build();
        when(repository.buscarPorEmail("ana@correo.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("secreto", "hash")).thenReturn(true);

        Usuario resultado = service.login("ana@correo.com", "secreto");

        assertThat(resultado).isEqualTo(u);
    }

    @Test
    void login_contrasenaIncorrecta_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(1L).email("ana@correo.com")
                .contrasenaHash("hash").estado(EstadoUsuario.ACTIVO).build();
        when(repository.buscarPorEmail("ana@correo.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("mala", "hash")).thenReturn(false);

        assertThatThrownBy(() -> service.login("ana@correo.com", "mala"))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void login_emailInexistente_lanzaExcepcion() {
        when(repository.buscarPorEmail("nadie@correo.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login("nadie@correo.com", "secreto"))
                .isInstanceOf(CredencialesInvalidasException.class);
    }

    @Test
    void login_usuarioInactivo_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(1L).email("ana@correo.com")
                .contrasenaHash("hash").estado(EstadoUsuario.INACTIVO).build();
        when(repository.buscarPorEmail("ana@correo.com")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("secreto", "hash")).thenReturn(true);

        assertThatThrownBy(() -> service.login("ana@correo.com", "secreto"))
                .isInstanceOf(EstadoInvalidoException.class);
    }
}
