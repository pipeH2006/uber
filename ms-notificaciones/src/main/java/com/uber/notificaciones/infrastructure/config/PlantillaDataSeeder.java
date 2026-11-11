package com.uber.notificaciones.infrastructure.config;

import com.uber.notificaciones.application.port.out.PlantillaRepositoryPort;
import com.uber.notificaciones.domain.model.Plantilla;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Inserta las plantillas por defecto al arrancar la aplicación.
 */
@Configuration
@RequiredArgsConstructor
public class PlantillaDataSeeder {

    private final PlantillaRepositoryPort plantillaRepository;

    @Bean
    public CommandLineRunner seedPlantillas() {
        return args -> {
            if (plantillaRepository.buscarPorCodigo("BIENVENIDA").isEmpty()) {
                plantillaRepository.guardar(Plantilla.builder()
                        .codigo("BIENVENIDA")
                        .asunto("Bienvenida")
                        .mensaje("Hola {nombre}, bienvenido a Uber")
                        .build());
            }
            if (plantillaRepository.buscarPorCodigo("VIAJE_CONFIRMADO").isEmpty()) {
                plantillaRepository.guardar(Plantilla.builder()
                        .codigo("VIAJE_CONFIRMADO")
                        .asunto("Viaje confirmado")
                        .mensaje("Tu viaje ha sido confirmado. {detalle}")
                        .build());
            }
        };
    }
}
