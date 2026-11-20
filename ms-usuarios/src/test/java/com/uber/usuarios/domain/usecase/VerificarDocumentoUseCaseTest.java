package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.DocumentoNoVerificableException;
import com.uber.usuarios.domain.exception.EstadoInvalidoException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoDocumento;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Rol;
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
class VerificarDocumentoUseCaseTest {
    @Mock
    private UsuarioGateway usuarioGateway;

    private VerificarDocumentoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new VerificarDocumentoUseCase(usuarioGateway);
    }

    @Test
    void verificarDocumento_conductor_verifica() {
        Usuario u = Usuario.builder().id(1L).rol(Rol.CONDUCTOR)
                .estado(EstadoUsuario.ACTIVO).estadoDocumento(EstadoDocumento.PENDIENTE).build();
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(usuarioGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(useCase.verificarDocumento(1L).getEstadoDocumento())
                .isEqualTo(EstadoDocumento.VERIFICADO);
    }

    @Test
    void verificarDocumento_pasajero_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(2L).rol(Rol.PASAJERO).estado(EstadoUsuario.ACTIVO).build();
        when(usuarioGateway.buscarPorId(2L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> useCase.verificarDocumento(2L))
                .isInstanceOf(DocumentoNoVerificableException.class);
    }

    @Test
    void verificarDocumento_conductorSuspendido_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(3L).rol(Rol.CONDUCTOR).estado(EstadoUsuario.SUSPENDIDO).build();
        when(usuarioGateway.buscarPorId(3L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> useCase.verificarDocumento(3L))
                .isInstanceOf(EstadoInvalidoException.class);
    }

    @Test
    void verificarDocumento_inexistente_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.verificarDocumento(99L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
