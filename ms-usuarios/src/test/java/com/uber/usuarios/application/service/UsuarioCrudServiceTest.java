package com.uber.usuarios.application.service;

import com.uber.usuarios.application.port.out.UsuarioRepositoryPort;
import com.uber.usuarios.domain.exception.EmailYaRegistradoException;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioCrudServiceTest {

    @Mock
    private UsuarioRepositoryPort repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UsuarioCrudService service;

    @BeforeEach
    void setUp() {
        service = new UsuarioCrudService(repository, passwordEncoder);
    }

    @Test
    void registrar_hasheaContrasenaYAsignaEstadoInicial_pasajero() {
        Usuario u = Usuario.builder()
                .nombre("Ana")
                .email("ana@correo.com")
                .contrasenaHash("secreto")
                .rol(Rol.PASAJERO)
                .build();
        when(repository.existePorEmail("ana@correo.com")).thenReturn(false);
        when(passwordEncoder.encode("secreto")).thenReturn("$2a$hashed");
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = service.registrar(u);

        assertThat(resultado.getContrasenaHash()).isEqualTo("$2a$hashed");
        assertThat(resultado.getEstado()).isEqualTo(EstadoUsuario.PENDIENTE);
        assertThat(resultado.getEstadoDocumento()).isEqualTo(EstadoDocumento.NO_APLICA);
        assertThat(resultado.getFechaRegistro()).isNotNull();
    }

    @Test
    void registrar_conductorAsignaDocumentoPendiente() {
        Usuario u = Usuario.builder()
                .nombre("Luis")
                .email("luis@correo.com")
                .contrasenaHash("secreto")
                .rol(Rol.CONDUCTOR)
                .build();
        when(repository.existePorEmail("luis@correo.com")).thenReturn(false);
        when(passwordEncoder.encode("secreto")).thenReturn("hash");
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario resultado = service.registrar(u);

        assertThat(resultado.getEstadoDocumento()).isEqualTo(EstadoDocumento.PENDIENTE);
    }

    @Test
    void registrar_emailDuplicado_lanzaExcepcion() {
        Usuario u = Usuario.builder().nombre("Ana").email("dup@correo.com").contrasenaHash("x").build();
        when(repository.existePorEmail("dup@correo.com")).thenReturn(true);

        assertThatThrownBy(() -> service.registrar(u))
                .isInstanceOf(EmailYaRegistradoException.class);
    }

    @Test
    void registrar_emailNulo_lanzaExcepcion() {
        Usuario u = Usuario.builder().nombre("Ana").contrasenaHash("x").build();

        assertThatThrownBy(() -> service.registrar(u))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void buscarPorId_existente_devuelveUsuario() {
        Usuario u = Usuario.builder().id(1L).nombre("Ana").build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(u));

        assertThat(service.buscarPorId(1L)).isEqualTo(u);
    }

    @Test
    void buscarPorId_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void listar_devuelveTodos() {
        when(repository.listar()).thenReturn(List.of(new Usuario(), new Usuario()));

        assertThat(service.listar()).hasSize(2);
    }

    @Test
    void actualizar_cambiaNombreYTelefono() {
        Usuario existente = Usuario.builder().id(1L).nombre("Ana").email("ana@correo.com").build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Usuario datos = Usuario.builder().nombre("Ana María").telefono("3001234567").build();
        Usuario resultado = service.actualizar(1L, datos);

        assertThat(resultado.getNombre()).isEqualTo("Ana María");
        assertThat(resultado.getTelefono()).isEqualTo("3001234567");
    }

    @Test
    void actualizar_emailDuplicado_lanzaExcepcion() {
        Usuario existente = Usuario.builder().id(1L).nombre("Ana").email("ana@correo.com").build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repository.existePorEmail("otro@correo.com")).thenReturn(true);

        Usuario datos = Usuario.builder().email("otro@correo.com").build();
        assertThatThrownBy(() -> service.actualizar(1L, datos))
                .isInstanceOf(EmailYaRegistradoException.class);
    }

    @Test
    void eliminar_existente_elimina() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(new Usuario()));

        service.eliminar(1L);

        verify(repository).eliminar(1L);
    }

    @Test
    void eliminar_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
