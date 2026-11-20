package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.CalificacionGateway;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalificarUsuarioUseCaseTest {
    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private CalificacionGateway calificacionGateway;

    private CalificarUsuarioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CalificarUsuarioUseCase(usuarioGateway, calificacionGateway);
    }

    @Test
    void calificar_puntajeValido_guardaCalificacion() {
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(usuarioGateway.buscarPorId(2L)).thenReturn(Optional.of(new Usuario()));
        when(calificacionGateway.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.calificar(1L, 2L, 5, "Excelente");

        verify(calificacionGateway).guardar(any());
    }

    @Test
    void calificar_puntajeFueraDeRango_lanzaExcepcion() {
        assertThatThrownBy(() -> useCase.calificar(1L, 2L, 6, "x"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> useCase.calificar(1L, 2L, 0, "x"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void calificar_usuarioInexistente_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.calificar(99L, 2L, 5, "x"))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void calificar_calificadorInexistente_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(usuarioGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.calificar(1L, 99L, 5, "x"))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
