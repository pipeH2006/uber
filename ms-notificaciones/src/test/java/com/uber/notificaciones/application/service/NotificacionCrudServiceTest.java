package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.out.NotificacionRepositoryPort;
import com.uber.notificaciones.domain.exception.NotificacionNoEncontradaException;
import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.TipoNotificacion;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionCrudServiceTest {

    @Mock
    private NotificacionRepositoryPort repository;

    private NotificacionCrudService service;

    @BeforeEach
    void setUp() {
        service = new NotificacionCrudService(repository);
    }

    @Test
    void crear_asignaDefaultsYGuarda() {
        Notificacion n = Notificacion.builder()
                .destinatario("123456")
                .mensaje("Hola")
                .build();
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Notificacion resultado = service.crear(n);

        assertThat(resultado.getCanal()).isEqualTo(Canal.TELEGRAM);
        assertThat(resultado.getTipo()).isEqualTo(TipoNotificacion.GENERICO);
        assertThat(resultado.getEstado()).isEqualTo(EstadoNotificacion.PENDIENTE);
    }

    @Test
    void crear_destinatarioNulo_lanzaExcepcion() {
        Notificacion n = Notificacion.builder().mensaje("Hola").build();

        assertThatThrownBy(() -> service.crear(n))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void crear_mensajeNulo_lanzaExcepcion() {
        Notificacion n = Notificacion.builder().destinatario("123456").build();

        assertThatThrownBy(() -> service.crear(n))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void buscarPorId_existente_devuelveNotificacion() {
        Notificacion n = Notificacion.builder().id(1L).destinatario("123456").build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(n));

        assertThat(service.buscarPorId(1L)).isEqualTo(n);
    }

    @Test
    void buscarPorId_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.buscarPorId(99L))
                .isInstanceOf(NotificacionNoEncontradaException.class);
    }

    @Test
    void listar_devuelveTodas() {
        when(repository.listar()).thenReturn(List.of(new Notificacion(), new Notificacion()));

        assertThat(service.listar()).hasSize(2);
    }

    @Test
    void actualizar_cambiaCampos() {
        Notificacion existente = Notificacion.builder()
                .id(1L).destinatario("123456").mensaje("Antes").estado(EstadoNotificacion.PENDIENTE).build();
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repository.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        Notificacion datos = Notificacion.builder()
                .destinatario("654321").mensaje("Después")
                .tipo(TipoNotificacion.VIAJE_FINALIZADO).estado(EstadoNotificacion.ENVIADA).build();
        Notificacion resultado = service.actualizar(1L, datos);

        assertThat(resultado.getDestinatario()).isEqualTo("654321");
        assertThat(resultado.getMensaje()).isEqualTo("Después");
        assertThat(resultado.getTipo()).isEqualTo(TipoNotificacion.VIAJE_FINALIZADO);
        assertThat(resultado.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
    }

    @Test
    void actualizar_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar(99L, new Notificacion()))
                .isInstanceOf(NotificacionNoEncontradaException.class);
    }

    @Test
    void eliminar_existente_elimina() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(new Notificacion()));

        service.eliminar(1L);

        verify(repository).eliminar(1L);
    }

    @Test
    void eliminar_inexistente_lanzaExcepcion() {
        when(repository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar(99L))
                .isInstanceOf(NotificacionNoEncontradaException.class);
    }
}
