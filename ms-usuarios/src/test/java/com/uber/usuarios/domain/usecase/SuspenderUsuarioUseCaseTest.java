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
class SuspenderUsuarioUseCaseTest {
    @Mock
    private UsuarioGateway usuarioGateway;

    private SuspenderUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new SuspenderUsuarioUseCase(usuarioGateway);
    }

    @Test
    void suspender_usuarioActivo_loSuspende() {
        Usuario u = Usuario.builder().id(1L).estado(EstadoUsuario.ACTIVO).build();
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(usuarioGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(useCase.suspender(1L).getEstado()).isEqualTo(EstadoUsuario.SUSPENDIDO);
    }

    @Test
    void suspender_usuarioPendiente_tambienSePuedeSuspender() {
        Usuario u = Usuario.builder().id(1L).estado(EstadoUsuario.PENDIENTE).build();
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(usuarioGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(useCase.suspender(1L).getEstado()).isEqualTo(EstadoUsuario.SUSPENDIDO);
    }

    @Test
    void suspender_usuarioYaSuspendido_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(1L).estado(EstadoUsuario.SUSPENDIDO).build();
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> useCase.suspender(1L)).isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void suspender_usuarioInexistente_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.suspender(99L)).isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
