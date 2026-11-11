package com.uber.notificaciones.application.service;

import com.uber.notificaciones.application.port.in.EnviarNotificacionUseCase;
import com.uber.notificaciones.application.port.out.PlantillaRepositoryPort;
import com.uber.notificaciones.domain.exception.PlantillaNoEncontradaException;
import com.uber.notificaciones.domain.model.Notificacion;
import com.uber.notificaciones.domain.model.Plantilla;
import com.uber.notificaciones.domain.model.TipoNotificacion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarConPlantillaServiceTest {

    @Mock
    private PlantillaRepositoryPort plantillaRepository;

    @Mock
    private EnviarNotificacionUseCase enviarNotificacion;

    private EnviarConPlantillaService service;

    @BeforeEach
    void setUp() {
        service = new EnviarConPlantillaService(plantillaRepository, enviarNotificacion);
    }

    @Test
    void enviarConPlantilla_reemplazaVariables() {
        Plantilla plantilla = Plantilla.builder()
                .codigo("BIENVENIDA").mensaje("Hola {nombre}, bienvenido a Uber").build();
        when(plantillaRepository.buscarPorCodigo("BIENVENIDA")).thenReturn(Optional.of(plantilla));
        when(enviarNotificacion.enviar(eq("123456"), eq("Hola Ana, bienvenido a Uber"), eq(TipoNotificacion.GENERICO)))
                .thenReturn(new Notificacion());

        service.enviarConPlantilla("BIENVENIDA", "123456", Map.of("nombre", "Ana"));

        verify(enviarNotificacion).enviar("123456", "Hola Ana, bienvenido a Uber", TipoNotificacion.GENERICO);
    }

    @Test
    void enviarConPlantilla_variablesNulas_mantieneMarcadores() {
        Plantilla plantilla = Plantilla.builder()
                .codigo("BIENVENIDA").mensaje("Hola {nombre}, bienvenido a Uber").build();
        when(plantillaRepository.buscarPorCodigo("BIENVENIDA")).thenReturn(Optional.of(plantilla));
        when(enviarNotificacion.enviar(eq("123456"), eq("Hola {nombre}, bienvenido a Uber"), eq(TipoNotificacion.GENERICO)))
                .thenReturn(new Notificacion());

        service.enviarConPlantilla("BIENVENIDA", "123456", null);

        verify(enviarNotificacion).enviar("123456", "Hola {nombre}, bienvenido a Uber", TipoNotificacion.GENERICO);
    }

    @Test
    void enviarConPlantilla_plantillaInexistente_lanzaExcepcion() {
        when(plantillaRepository.buscarPorCodigo("INEXISTENTE")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.enviarConPlantilla("INEXISTENTE", "123456", Map.of()))
                .isInstanceOf(PlantillaNoEncontradaException.class);
    }
}
