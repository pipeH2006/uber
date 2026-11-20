package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
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
class ActivarUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    private ActivarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ActivarUsuarioUseCase(usuarioGateway);
    }

    @Test
    void activar_usuarioPendiente_loActiva() {
        Usuario u = Usuario.builder().id(1L).estado(EstadoUsuario.PENDIENTE).build();
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(usuarioGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(useCase.activar(1L).getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void activar_usuarioInactivo_loActiva() {
        Usuario u = Usuario.builder().id(2L).estado(EstadoUsuario.INACTIVO).build();
        when(usuarioGateway.buscarPorId(2L)).thenReturn(Optional.of(u));
        when(usuarioGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(useCase.activar(2L).getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void activar_usuarioSuspendido_loReactiva() {
        Usuario u = Usuario.builder().id(3L).estado(EstadoUsuario.SUSPENDIDO).build();
        when(usuarioGateway.buscarPorId(3L)).thenReturn(Optional.of(u));
        when(usuarioGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(useCase.activar(3L).getEstado()).isEqualTo(EstadoUsuario.ACTIVO);
    }

    @Test
    void activar_usuarioYaActivo_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(1L).estado(EstadoUsuario.ACTIVO).build();
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> useCase.activar(1L)).isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void activar_usuarioInexistente_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.activar(99L)).isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
