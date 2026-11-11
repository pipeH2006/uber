package com.uber.notificaciones.infrastructure.adapter.out.persistence;

import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.Plantilla;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({NotificacionRepositoryAdapter.class, PlantillaRepositoryAdapter.class})
class PersistenceAdaptersTest {

    @Autowired
    private NotificacionRepositoryAdapter notificacionAdapter;

    @Autowired
    private PlantillaRepositoryAdapter plantillaAdapter;

    private Notificacion notificacionDomain() {
        return Notificacion.builder()
                .destinatario("123456")
                .canal(Canal.TELEGRAM)
                .tipo(TipoNotificacion.VIAJE_SOLICITADO)
                .mensaje("Tu viaje ha sido solicitado")
                .estado(EstadoNotificacion.ENVIADA)
                .fechaEnvio(LocalDateTime.now())
                .intentos(1)
                .build();
    }

    @Test
    void notificacion_guardarYBbuscarPorId_mapeaCorrectamente() {
        Notificacion guardada = notificacionAdapter.guardar(notificacionDomain());

        Optional<Notificacion> encontrada = notificacionAdapter.buscarPorId(guardada.getId());

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getDestinatario()).isEqualTo("123456");
        assertThat(encontrada.get().getCanal()).isEqualTo(Canal.TELEGRAM);
        assertThat(encontrada.get().getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(encontrada.get().getIntentos()).isEqualTo(1);
    }

    @Test
    void notificacion_listar_devuelveTodas() {
        notificacionAdapter.guardar(notificacionDomain());
        notificacionAdapter.guardar(notificacionDomain());

        List<Notificacion> notificaciones = notificacionAdapter.listar();

        assertThat(notificaciones).hasSize(2);
    }

    @Test
    void notificacion_eliminar_borraLaNotificacion() {
        Notificacion guardada = notificacionAdapter.guardar(notificacionDomain());

        notificacionAdapter.eliminar(guardada.getId());

        assertThat(notificacionAdapter.buscarPorId(guardada.getId())).isEmpty();
    }

    @Test
    void plantilla_guardarBuscarPorCodigoYListar() {
        plantillaAdapter.guardar(Plantilla.builder()
                .codigo("BIENVENIDA").asunto("Bienvenida")
                .mensaje("Hola {nombre}, bienvenido a Uber").build());
        plantillaAdapter.guardar(Plantilla.builder()
                .codigo("VIAJE_CONFIRMADO").asunto("Viaje confirmado")
                .mensaje("Tu viaje ha sido confirmado. {detalle}").build());

        Optional<Plantilla> encontrada = plantillaAdapter.buscarPorCodigo("BIENVENIDA");

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getMensaje()).contains("{nombre}");
        assertThat(plantillaAdapter.listar()).hasSize(2);
        assertThat(plantillaAdapter.buscarPorCodigo("INEXISTENTE")).isEmpty();
    }
}
