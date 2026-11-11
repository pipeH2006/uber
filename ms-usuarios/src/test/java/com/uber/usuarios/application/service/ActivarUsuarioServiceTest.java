package com.uber.usuarios.application.service;

import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActivarUsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort repository;

    private ActivarUsuarioService service;

    @BeforeEach
    void setUp() {
        service = new ActivarUsuarioService(repository);
    }

    @Test
    void activar_usuarioPendiente_loActiva() {
        Usuario u = Usuario.builder().id(1L).estado(EstadoUsuario.PENDIENTE).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = service.activar(1L);

        assertThat(resultado.getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void activar_usuarioInactivo_loActiva() {
        Usuario u = Usuario.builder().id(2L).estado(EstadoUsuario.INACTIVO).build();
        when(repository.buscarPorId(2L)).thenReturn(Optional.of(u));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.activar(2L).getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void activar_usuarioYaActivo_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(1L).estado(EstadoUsuario.ACTIVO).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> service.activar(1L))
                .isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void activar_usuarioInexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.activar(99L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
