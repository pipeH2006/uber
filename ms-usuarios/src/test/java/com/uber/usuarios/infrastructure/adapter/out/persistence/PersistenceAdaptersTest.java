package com.uber.usuarios.infrastructure.adapter.out.persistence;

import com.uber.usuarios.domain.model.Calificacion;
import com.uber.usuarios.domain.model.EstadoDocumento;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Rol;
import com.uber.usuarios.domain.model.Usuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({UsuarioRepositoryAdapter.class, CalificacionRepositoryAdapter.class})
class PersistenceAdaptersTest {

    @Autowired
    private UsuarioRepositoryAdapter usuarioAdapter;

    @Autowired
    private CalificacionRepositoryAdapter calificacionAdapter;

    private Usuario usuarioDomain() {
        return Usuario.builder()
                .nombre("Ana")
                .email("ana@correo.com")
                .telefono("3001234567")
                .contrasenaHash("hash")
                .rol(Rol.CONDUCTOR)
                .estado(EstadoUsuario.ACTIVO)
                .estadoDocumento(EstadoDocumento.VERIFICADO)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    @Test
    void guardarYBbuscarPorId_mapeaCorrectamente() {
        Usuario guardado = usuarioAdapter.guardar(usuarioDomain());

        Optional<Usuario> encontrado = usuarioAdapter.buscarPorId(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("ana@correo.com");
        assertThat(encontrado.get().getRol()).isEqualTo(Rol.CONDUCTOR);
    }

    @Test
    void buscarPorEmail_devuelveUsuario() {
        usuarioAdapter.guardar(usuarioDomain());

        Optional<Usuario> encontrado = usuarioAdapter.buscarPorEmail("ana@correo.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Ana");
    }

    @Test
    void existePorEmail_detectaDuplicados() {
        usuarioAdapter.guardar(usuarioDomain());

        assertThat(usuarioAdapter.existePorEmail("ana@correo.com")).isTrue();
        assertThat(usuarioAdapter.existePorEmail("otro@correo.com")).isFalse();
    }

    @Test
    void listar_devuelveTodosLosUsuarios() {
        usuarioAdapter.guardar(usuarioDomain());
        usuarioAdapter.guardar(Usuario.builder().nombre("Luis").email("luis@correo.com")
                .contrasenaHash("hash").rol(Rol.PASAJERO).estado(EstadoUsuario.PENDIENTE)
                .estadoDocumento(EstadoDocumento.NO_APLICA).build());

        List<Usuario> usuarios = usuarioAdapter.listar();

        assertThat(usuarios).hasSize(2);
    }

    @Test
    void eliminar_borraElUsuario() {
        Usuario guardado = usuarioAdapter.guardar(usuarioDomain());

        usuarioAdapter.eliminar(guardado.getId());

        assertThat(usuarioAdapter.buscarPorId(guardado.getId())).isEmpty();
    }

    @Test
    void calificaciones_guardarYBbuscarPorUsuarioId() {
        Usuario guardado = usuarioAdapter.guardar(usuarioDomain());

        calificacionAdapter.guardar(Calificacion.builder()
                .usuarioId(guardado.getId())
                .calificadorId(2L)
                .puntaje(5)
                .comentario("Buen conductor")
                .fecha(LocalDateTime.now())
                .build());
        calificacionAdapter.guardar(Calificacion.builder()
                .usuarioId(guardado.getId())
                .calificadorId(3L)
                .puntaje(4)
                .fecha(LocalDateTime.now())
                .build());

        List<Calificacion> calificaciones = calificacionAdapter.buscarPorUsuarioId(guardado.getId());

        assertThat(calificaciones).hasSize(2);
        assertThat(calificaciones).allMatch(c -> c.getUsuarioId().equals(guardado.getId()));
    }
}
