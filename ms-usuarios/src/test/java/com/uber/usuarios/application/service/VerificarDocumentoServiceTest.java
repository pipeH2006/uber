package com.uber.usuarios.application.service;

import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.exception.DocumentoNoVerificableException;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoDocumento;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Rol;
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
class VerificarDocumentoServiceTest {

    @Mock
    private UsuarioRepositoryPort repository;

    private VerificarDocumentoService service;

    @BeforeEach
    void setUp() {
        service = new VerificarDocumentoService(repository);
    }

    @Test
    void verificarDocumento_conductor_verifica() {
        Usuario u = Usuario.builder().id(1L).rol(Rol.CONDUCTOR)
                .estado(EstadoUsuario.ACTIVO).estadoDocumento(EstadoDocumento.PENDIENTE).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(service.verificarDocumento(1L).getEstadoDocumento())
                .isEqualTo(EstadoDocumento.VERIFICADO);
    }

    @Test
    void verificarDocumento_pasajero_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(2L).rol(Rol.PASAJERO).estado(EstadoUsuario.ACTIVO).build();
        when(repository.buscarPorId(2L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> service.verificarDocumento(2L))
                .isInstanceOf(DocumentoNoVerificableException.class);
    }

    @Test
    void verificarDocumento_conductorSuspendido_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(3L).rol(Rol.CONDUCTOR).estado(EstadoUsuario.SUSPENDIDO).build();
        when(repository.buscarPorId(3L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> service.verificarDocumento(3L))
                .isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void verificarDocumento_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.verificarDocumento(99L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
