package com.uber.usuarios.application.config;

import com.uber.usuarios.domain.model.gateway.CalificacionGateway;
import com.uber.usuarios.domain.model.gateway.UsuarioGateway;
import com.uber.usuarios.domain.usecase.ActivarUsuarioUseCase;
import com.uber.usuarios.domain.usecase.CalificarUsuarioUseCase;
import com.uber.usuarios.domain.usecase.DesactivarUsuarioUseCase;
import com.uber.usuarios.domain.usecase.LoginUseCase;
import com.uber.usuarios.domain.usecase.ObtenerCalificacionPromedioUseCase;
import com.uber.usuarios.domain.usecase.UsuarioCrudUseCase;
import com.uber.usuarios.domain.usecase.VerificarDocumentoUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Registra los casos de uso (domain/usecase) como beans de Spring,
 * manteniendo el dominio libre de anotaciones de Spring.
 */
@Configuration
public class UseCaseConfig {

    @Bean
    public UsuarioCrudUseCase usuarioCrudUseCase(UsuarioGateway usuarioGateway, PasswordEncoder passwordEncoder) {
        return new UsuarioCrudUseCase(usuarioGateway, passwordEncoder);
    }

    @Bean
    public LoginUseCase loginUseCase(UsuarioGateway usuarioGateway, PasswordEncoder passwordEncoder) {
        return new LoginUseCase(usuarioGateway, passwordEncoder);
    }

    @Bean
    public ActivarUsuarioUseCase activarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        return new ActivarUsuarioUseCase(usuarioGateway);
    }

    @Bean
    public DesactivarUsuarioUseCase desactivarUsuarioUseCase(UsuarioGateway usuarioGateway) {
        return new DesactivarUsuarioUseCase(usuarioGateway);
    }

    @Bean
    public VerificarDocumentoUseCase verificarDocumentoUseCase(UsuarioGateway usuarioGateway) {
        return new VerificarDocumentoUseCase(usuarioGateway);
    }

    @Bean
    public CalificarUsuarioUseCase calificarUsuarioUseCase(UsuarioGateway usuarioGateway,
                                                            CalificacionGateway calificacionGateway) {
        return new CalificarUsuarioUseCase(usuarioGateway, calificacionGateway);
    }

    @Bean
    public ObtenerCalificacionPromedioUseCase obtenerCalificacionPromedioUseCase(UsuarioGateway usuarioGateway,
                                                                                  CalificacionGateway calificacionGateway) {
        return new ObtenerCalificacionPromedioUseCase(usuarioGateway, calificacionGateway);
    }
}
