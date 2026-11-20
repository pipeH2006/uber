package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.DocumentoNoVerificableException;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.EstadoDocumento;
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
class RechazarDocumentoUseCaseTest {
    @Mock
    private UsuarioGateway usuarioGateway;

    private RechazarDocumentoUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new RechazarDocumentoUseCase(usuarioGateway);
    }

    @Test
    void rechazarDocumento_conductor_loMarcaRechazado() {
        Usuario u = Usuario.builder().id(1L).rol(Rol.CONDUCTOR).estadoDocumento(EstadoDocumento.PENDIENTE).build();
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(u));
        when(usuarioGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(useCase.rechazarDocumento(1L).getEstadoDocumento()).isEqualTo(EstadoDocumento.RECHAZADO);
    }

    @Test
    void rechazarDocumento_pasajero_lanzaExcepcion() {
        Usuario u = Usuario.builder().id(2L).rol(Rol.PASAJERO).build();
        when(usuarioGateway.buscarPorId(2L)).thenReturn(Optional.of(u));

        assertThatThrownBy(() -> useCase.rechazarDocumento(2L)).isInstanceOf(DocumentoNoVerificableException.class);
    }

    @Test
    void rechazarDocumento_usuarioInexistente_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.rechazarDocumento(99L)).isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
