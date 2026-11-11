package com.uber.viajes.infrastructure.adapter.out.persistence;

import com.uber.viajes.domain.model.EstadoViaje;
import com.uber.viajes.domain.model.Viaje;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ViajeRepositoryAdapter.class)
class ViajeRepositoryAdapterTest {

    @Autowired
    private ViajeRepositoryAdapter adapter;

    private Viaje viajeDomain() {
        return Viaje.builder()
                .pasajeroId(10L)
                .conductorId(20L)
                .origenLat(4.71)
                .origenLng(-74.07)
                .origenDireccion("Cra 7 # 71-21")
                .destinoLat(4.60)
                .destinoLng(-74.08)
                .destinoDireccion("Aeropuerto El Dorado")
                .distanciaKm(12.5)
                .duracionMin(20.0)
                .costoEstimado(new BigDecimal("13.50"))
                .costoFinal(new BigDecimal("13.50"))
                .moneda("USD")
                .estado(EstadoViaje.EN_CURSO)
                .fechaSolicitud(LocalDateTime.now())
                .fechaInicio(LocalDateTime.now())
                .build();
    }

    @Test
    void guardarYBuscarPorId_mapeaTodosLosCampos() {
        Viaje guardado = adapter.guardar(viajeDomain());

        Optional<Viaje> encontrado = adapter.buscarPorId(guardado.getId());

        assertThat(encontrado).isPresent();
        Viaje v = encontrado.get();
        assertThat(v.getPasajeroId()).isEqualTo(10L);
        assertThat(v.getConductorId()).isEqualTo(20L);
        assertThat(v.getOrigenLat()).isEqualTo(4.71);
        assertThat(v.getOrigenLng()).isEqualTo(-74.07);
        assertThat(v.getOrigenDireccion()).isEqualTo("Cra 7 # 71-21");
        assertThat(v.getDestinoLat()).isEqualTo(4.60);
        assertThat(v.getDestinoLng()).isEqualTo(-74.08);
        assertThat(v.getDestinoDireccion()).isEqualTo("Aeropuerto El Dorado");
        assertThat(v.getDistanciaKm()).isEqualTo(12.5);
        assertThat(v.getDuracionMin()).isEqualTo(20.0);
        assertThat(v.getCostoEstimado()).isEqualByComparingTo("13.50");
        assertThat(v.getCostoFinal()).isEqualByComparingTo("13.50");
        assertThat(v.getMoneda()).isEqualTo("USD");
        assertThat(v.getEstado()).isEqualTo(EstadoViaje.EN_CURSO);
        assertThat(v.getFechaSolicitud()).isNotNull();
        assertThat(v.getFechaInicio()).isNotNull();
        assertThat(v.getFechaFin()).isNull();
    }

    @Test
    void buscarPorId_inexistente_devuelveVacio() {
        assertThat(adapter.buscarPorId(999L)).isEmpty();
    }

    @Test
    void listar_devuelveTodos() {
        adapter.guardar(viajeDomain());
        adapter.guardar(Viaje.builder().pasajeroId(11L).conductorId(21L).estado(EstadoViaje.SOLICITADO).build());

        List<Viaje> viajes = adapter.listar();

        assertThat(viajes).hasSize(2);
    }

    @Test
    void eliminar_borraElViaje() {
        Viaje guardado = adapter.guardar(viajeDomain());

        adapter.eliminar(guardado.getId());

        assertThat(adapter.buscarPorId(guardado.getId())).isEmpty();
    }
}
