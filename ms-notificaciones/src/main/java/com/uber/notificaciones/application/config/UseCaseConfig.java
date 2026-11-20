package com.uber.notificaciones.application.config;

import com.uber.notificaciones.domain.model.gateway.NotificacionGateway;
import com.uber.notificaciones.domain.model.gateway.PlantillaGateway;
import com.uber.notificaciones.domain.model.gateway.RedactorGateway;
import com.uber.notificaciones.domain.model.gateway.TelegramGateway;
import com.uber.notificaciones.domain.model.gateway.UsuarioGateway;
import com.uber.notificaciones.domain.usecase.ConsultarEstadoNotificacionUseCase;
import com.uber.notificaciones.domain.usecase.EnviarConPlantillaUseCase;
import com.uber.notificaciones.domain.usecase.EnviarNotificacionMasivaUseCase;
import com.uber.notificaciones.domain.usecase.EnviarNotificacionUseCase;
import com.uber.notificaciones.domain.usecase.ListarPlantillasUseCase;
import com.uber.notificaciones.domain.usecase.NotificacionCrudUseCase;
import com.uber.notificaciones.domain.usecase.NotificacionSender;
import com.uber.notificaciones.domain.usecase.ReintentarNotificacionUseCase;
import com.uber.notificaciones.domain.usecase.VincularTelegramUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra los casos de uso (domain/usecase) como beans de Spring,
 * manteniendo el dominio libre de anotaciones de Spring.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public NotificacionSender notificacionSender(NotificacionGateway notificacionGateway,
                                                 TelegramGateway telegramGateway) {
        return new NotificacionSender(notificacionGateway, telegramGateway);
    }

    @Bean
    public NotificacionCrudUseCase notificacionCrudUseCase(NotificacionGateway notificacionGateway) {
        return new NotificacionCrudUseCase(notificacionGateway);
    }

    @Bean
    public EnviarNotificacionUseCase enviarNotificacionUseCase(NotificacionGateway notificacionGateway,
                                                               NotificacionSender notificacionSender,
                                                               RedactorGateway redactorGateway) {
        return new EnviarNotificacionUseCase(notificacionGateway, notificacionSender, redactorGateway);
    }

    @Bean
    public VincularTelegramUseCase vincularTelegramUseCase(TelegramGateway telegramGateway,
                                                           UsuarioGateway usuarioGateway) {
        return new VincularTelegramUseCase(telegramGateway, usuarioGateway);
    }

    @Bean
    public EnviarNotificacionMasivaUseCase enviarNotificacionMasivaUseCase(NotificacionGateway notificacionGateway,
                                                                           NotificacionSender notificacionSender) {
        return new EnviarNotificacionMasivaUseCase(notificacionGateway, notificacionSender);
    }

    @Bean
    public ConsultarEstadoNotificacionUseCase consultarEstadoNotificacionUseCase(NotificacionGateway notificacionGateway) {
        return new ConsultarEstadoNotificacionUseCase(notificacionGateway);
    }

    @Bean
    public ReintentarNotificacionUseCase reintentarNotificacionUseCase(NotificacionGateway notificacionGateway,
                                                                       NotificacionSender notificacionSender) {
        return new ReintentarNotificacionUseCase(notificacionGateway, notificacionSender);
    }

    @Bean
    public ListarPlantillasUseCase listarPlantillasUseCase(PlantillaGateway plantillaGateway) {
        return new ListarPlantillasUseCase(plantillaGateway);
    }

    @Bean
    public EnviarConPlantillaUseCase enviarConPlantillaUseCase(PlantillaGateway plantillaGateway,
                                                               EnviarNotificacionUseCase enviarNotificacionUseCase) {
        return new EnviarConPlantillaUseCase(plantillaGateway, enviarNotificacionUseCase);
    }
}
