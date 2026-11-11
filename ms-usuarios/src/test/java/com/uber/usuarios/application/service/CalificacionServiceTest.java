package com.uber.usuarios.application.service;

import com.uber.usuarios.application.port.in.CalificacionResumen;
import com.uber.usuarios.application.port.out.CalificacionRepositoryPort;
import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.exception.UsuarioNoEncontradoException;
import com.uber.usuarios.domain.model.Calificacion;
import com.uber.usuarios.domain.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalificacionServiceTest {

    @Mock
    private UsuarioRepositoryPort usuarioRepository;

    @Mock
    private CalificacionRepositoryPort calificacionRepository;

    private CalificacionService service;

    @BeforeEach
    void setUp() {
        service = new CalificacionService(usuarioRepository, calificacionRepository);
    }

    @Test
    void calificar_puntajeValido_guardaCalificacion() {
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(usuarioRepository.buscarPorId(2L)).thenReturn(Optional.of(new Usuario()));
        when(calificacionRepository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        service.calificar(1L, 2L, 5, "Excelente");

        org.mockito.Mockito.verify(calificacionRepository).guardar(any());
    }

    @Test
    void calificar_puntajeFueraDeRango_lanzaExcepcion() {
        assertThatThrownBy(() -> service.calificar(1L, 2L, 6, "x"))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> service.calificar(1L, 2L, 0, "x"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void calificar_usuarioInexistente_lanzaExcepcion() {
        when(usuarioRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.calificar(99L, 2L, 5, "x"))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void calificar_calificadorInexistente_lanzaExcepcion() {
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(usuarioRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.calificar(1L, 99L, 5, "x"))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void promedio_conCalificaciones_calculaCorrectamente() {
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(calificacionRepository.buscarPorUsuarioId(1L)).thenReturn(List.of(
                Calificacion.builder().puntaje(4).build(),
                Calificacion.builder().puntaje(5).build()
        ));

        CalificacionResumen resumen = service.promedio(1L);

        assertThat(resumen.promedio()).isEqualTo(4.5);
        assertThat(resumen.totalCalificaciones()).isEqualTo(2);
    }

    @Test
    void promedio_sinCalificaciones_devuelveCero() {
        when(usuarioRepository.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(calificacionRepository.buscarPorUsuarioId(1L)).thenReturn(List.of());

        CalificacionResumen resumen = service.promedio(1L);

        assertThat(resumen.promedio()).isEqualTo(0.0);
        assertThat(resumen.totalCalificaciones()).isZero();
    }

    @Test
    void promedio_usuarioInexistente_lanzaExcepcion() {
        when(usuarioRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.promedio(99L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
