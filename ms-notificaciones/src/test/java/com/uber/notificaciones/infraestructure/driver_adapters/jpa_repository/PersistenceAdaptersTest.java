package com.uber.notificaciones.infraestructure.driver_adapters.jpa_repository;

import com.uber.notificaciones.domain.model.Canal;
import com.uber.notificaciones.domain.model.EstadoNotificacion;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.Plantilla;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import com.uber.notificaciones.infraestructure.mapper.MapperNotificacion;
import com.uber.notificaciones.infraestructure.mapper.MapperPlantilla;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({NotificacionDataGatewayImpl.class, PlantillaDataGatewayImpl.class, MapperNotificacion.class, MapperPlantilla.class})
class PersistenceAdaptersTest {

    @Autowired
    private NotificacionDataGatewayImpl notificacionGateway;

    @Autowired
    private PlantillaDataGatewayImpl plantillaGateway;

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
        Notificacion guardada = notificacionGateway.guardar(notificacionDomain());

        Optional<Notificacion> encontrada = notificacionGateway.buscarPorId(guardada.getId());

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getDestinatario()).isEqualTo("123456");
        assertThat(encontrada.get().getCanal()).isEqualTo(Canal.TELEGRAM);
        assertThat(encontrada.get().getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(encontrada.get().getIntentos()).isEqualTo(1);
    }

    @Test
    void notificacion_listar_devuelveTodas() {
        notificacionGateway.guardar(notificacionDomain());
        notificacionGateway.guardar(notificacionDomain());

        List<Notificacion> notificaciones = notificacionGateway.listar();

        assertThat(notificaciones).hasSize(2);
    }

    @Test
    void notificacion_eliminar_borraLaNotificacion() {
        Notificacion guardada = notificacionGateway.guardar(notificacionDomain());

        notificacionGateway.eliminar(guardada.getId());

        assertThat(notificacionGateway.buscarPorId(guardada.getId())).isEmpty();
    }

    @Test
    void plantilla_guardarBuscarPorCodigoYListar() {
        plantillaGateway.guardar(Plantilla.builder()
                .codigo("BIENVENIDA").asunto("Bienvenida")
                .mensaje("Hola {nombre}, bienvenido a Uber").build());
        plantillaGateway.guardar(Plantilla.builder()
                .codigo("VIAJE_CONFIRMADO").asunto("Viaje confirmado")
                .mensaje("Tu viaje ha sido confirmado. {detalle}").build());

        Optional<Plantilla> encontrada = plantillaGateway.buscarPorCodigo("BIENVENIDA");

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getMensaje()).contains("{nombre}");
        assertThat(plantillaGateway.listar()).hasSize(2);
        assertThat(plantillaGateway.buscarPorCodigo("INEXISTENTE")).isEmpty();
    }
}
