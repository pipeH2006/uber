package com.uber.notificaciones.application.config;

import com.uber.notificaciones.domain.model.Plantilla;
import com.uber.notificaciones.domain.model.gateway.PlantillaGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class PlantillaDataSeeder {
    private final PlantillaGateway plantillaGateway;

    @Bean
    public CommandLineRunner seedPlantillas() {
        return args -> {
            if (plantillaGateway.buscarPorCodigo("BIENVENIDA").isEmpty()) {
                plantillaGateway.guardar(Plantilla.builder()
                        .codigo("BIENVENIDA")
                        .asunto("Bienvenida")
                        .mensaje("Hola {nombre}, bienvenido a Uber")
                        .build());
            }
            if (plantillaGateway.buscarPorCodigo("VIAJE_CONFIRMADO").isEmpty()) {
                plantillaGateway.guardar(Plantilla.builder()
                        .codigo("VIAJE_CONFIRMADO")
                        .asunto("Viaje confirmado")
                        .mensaje("Tu viaje ha sido confirmado. {detalle}")
                        .build());
            }
        };
    }
}
