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
class DesactivarUsuarioServiceTest {

    @Mock
    private UsuarioRepositoryPort repository;

    private DesactivarUsuarioService service;

    @BeforeEach
    void setUp() {
        service = new DesactivarUsuarioService(repository);
    }

    @Test
    void desactivar_usuarioActivo_loDesactiva() {
        Usuario u = Usuario.builder().id(1L).estado(EstadoUsuario.ACTIVO).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.desactivar(1L).getEstado()).isEqualTo(EstadoUsuario.INACTIVO);
    }

    @Test
    void desactivar_usuarioInactivo_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(1L).estado(EstadoUsuario.INACTIVO).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> service.desactivar(1L))
                .isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void desactivar_usuarioInexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.desactivar(99L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
