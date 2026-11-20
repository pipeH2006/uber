package com.uber.usuarios.infraestructure.driver_adapters.jpa_repository;

import com.uber.usuarios.domain.model.Calificacion;
import com.uber.usuarios.domain.model.EstadoDocumento;
import com.uber.usuarios.domain.model.EstadoUsuario;
import com.uber.usuarios.domain.model.Rol;
import com.uber.usuarios.domain.model.Usuario;
import com.uber.usuarios.infraestructure.mapper.MapperCalificacion;
import com.uber.usuarios.infraestructure.mapper.MapperUsuario;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({UsuarioDataGatewayImpl.class, CalificacionDataGatewayImpl.class, MapperUsuario.class, MapperCalificacion.class})
class PersistenceAdaptersTest {

    @Autowired
    private UsuarioDataGatewayImpl usuarioGateway;

    @Autowired
    private CalificacionDataGatewayImpl calificacionGateway;

    private Usuario usuarioDomain() {
        return Usuario.builder()
                .nombre("Ana")
                .email("ana@correo.com")
                .telefono("3001234567")
                .telegramChatId("123456789")
                .contrasenaHash("hash")
                .rol(Rol.CONDUCTOR)
                .estado(EstadoUsuario.ACTIVO)
                .estadoDocumento(EstadoDocumento.VERIFICADO)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    @Test
    void guardarYBbuscarPorId_mapeaCorrectamente() {
        Usuario guardado = usuarioGateway.guardar(usuarioDomain());

        Optional<Usuario> encontrado = usuarioGateway.buscarPorId(guardado.getId());

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getEmail()).isEqualTo("ana@correo.com");
        assertThat(encontrado.get().getRol()).isEqualTo(Rol.CONDUCTOR);
        assertThat(encontrado.get().getTelegramChatId()).isEqualTo("123456789");
    }

    @Test
    void buscarPorEmail_devuelveUsuario() {
        usuarioGateway.guardar(usuarioDomain());

        Optional<Usuario> encontrado = usuarioGateway.buscarPorEmail("ana@correo.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNombre()).isEqualTo("Ana");
    }

    @Test
    void existePorEmail_detectaDuplicados() {
        usuarioGateway.guardar(usuarioDomain());

        assertThat(usuarioGateway.existePorEmail("ana@correo.com")).isTrue();
        assertThat(usuarioGateway.existePorEmail("otro@correo.com")).isFalse();
    }

    @Test
    void listar_devuelveTodosLosUsuarios() {
        usuarioGateway.guardar(usuarioDomain());
        usuarioGateway.guardar(Usuario.builder().nombre("Luis").email("luis@correo.com")
                .contrasenaHash("hash").rol(Rol.PASAJERO).estado(EstadoUsuario.PENDIENTE)
                .estadoDocumento(EstadoDocumento.NO_APLICA).build());

        List<Usuario> usuarios = usuarioGateway.listar();

        assertThat(usuarios).hasSize(2);
    }

    @Test
    void eliminar_borraElUsuario() {
        Usuario guardado = usuarioGateway.guardar(usuarioDomain());

        usuarioGateway.eliminar(guardado.getId());

        assertThat(usuarioGateway.buscarPorId(guardado.getId())).isEmpty();
    }

    @Test
    void calificaciones_guardarYBbuscarPorUsuarioId() {
        Usuario guardado = usuarioGateway.guardar(usuarioDomain());

        calificacionGateway.guardar(Calificacion.builder()
                .usuarioId(guardado.getId())
                .calificadorId(2L)
                .puntaje(5)
                .comentario("Buen conductor")
                .fecha(LocalDateTime.now())
                .build());
        calificacionGateway.guardar(Calificacion.builder()
                .usuarioId(guardado.getId())
                .calificadorId(3L)
                .puntaje(4)
                .fecha(LocalDateTime.now())
                .build());

        List<Calificacion> calificaciones = calificacionGateway.buscarPorUsuarioId(guardado.getId());

        assertThat(calificaciones).hasSize(2);
        assertThat(calificaciones).allMatch(c -> c.getUsuarioId().equals(guardado.getId()));
    }
}
