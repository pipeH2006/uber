package com.uber.usuarios.domain.usecase;

import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.Calificacion;
import com.uber.usuarios.domain.model.CalificacionResumen;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.domain.model.gateway.CalificacionGateway;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerCalificacionPromedioUseCaseTest {
    @Mock
    private UsuarioGateway usuarioGateway;

    @Mock
    private CalificacionGateway calificacionGateway;

    private ObtenerCalificacionPromedioUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ObtenerCalificacionPromedioUseCase(usuarioGateway, calificacionGateway);
    }

    @Test
    void promedio_conCalificaciones_calculaCorrectamente() {
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(calificacionGateway.buscarPorUsuarioId(1L)).thenReturn(List.of(
                Calificacion.builder().puntaje(4).build(),
                Calificacion.builder().puntaje(5).build()
        ));

        CalificacionResumen resumen = useCase.promedio(1L);

        assertThat(resumen.promedio()).isEqualTo(4.5);
        assertThat(resumen.totalCalificaciones()).isEqualTo(2);
    }

    @Test
    void promedio_sinCalificaciones_devuelveCero() {
        when(usuarioGateway.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(calificacionGateway.buscarPorUsuarioId(1L)).thenReturn(List.of());

        CalificacionResumen resumen = useCase.promedio(1L);

        assertThat(resumen.promedio()).isEqualTo(0.0);
        assertThat(resumen.totalCalificaciones()).isZero();
    }

    @Test
    void promedio_usuarioInexistente_lanzaExcepcion() {
        when(usuarioGateway.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.promedio(99L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
