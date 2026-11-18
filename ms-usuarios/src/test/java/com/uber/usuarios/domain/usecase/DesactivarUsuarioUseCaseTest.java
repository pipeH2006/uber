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
class DesactivarUsuarioUseCaseTest {

    @Mock
    private UsuarioGateway usuarioGateway;

    private DesactivarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new DesactivarUsuarioUseCase(usuarioGateway);
    }

    @Test
    void desactivar_usuarioActivo_loDesactiva() {
        Usuario u = Usuario.builder().id(1L).estado(EstadoUsuario.ACTIVO).build();
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(usuarioGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(useCase.desactivar(1L).getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
    }

    @Test
    void desactivar_usuarioInactivo_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(1L).estado(EstadoUsuario.INACTIVO).build();
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> useCase.desactivar(1L)).isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void desactivar_usuarioInexistente_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.desactivar(99L)).isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
