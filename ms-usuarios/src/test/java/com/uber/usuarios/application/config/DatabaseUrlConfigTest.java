package com.uber.usuarios.application.config;

import com.uber.usuarios.application.config.DatabaseUrlConfig.DatabaseUrl;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DatabaseUrlConfigTest {
    @Test
    void parse_urlPostgresql_convierteAJdbc() {
        DatabaseUrl result = DatabaseUrlConfig.parse("postgresql://usuario:clave@host-ejemplo:5432/midb");

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://host-ejemplo:5432/midb");
        assertThat(result.username()).isEqualTo("usuario");
        assertThat(result.password()).isEqualTo("clave");
    }

    @Test
    void parse_urlPostgresSinPuerto_usa5432() {
        DatabaseUrl result = DatabaseUrlConfig.parse("postgres://user:pass@host/midb");

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://host:5432/midb");
        assertThat(result.username()).isEqualTo("user");
        assertThat(result.password()).isEqualTo("pass");
    }

    @Test
    void parse_urlNeonConSslmode_conservaLaQuery() {
        DatabaseUrl result = DatabaseUrlConfig.parse(
                "postgresql://neondb_owner:secreto@ep-algo-pooler.us-east-2.aws.neon.tech/uber?sslmode=require");

        assertThat(result.jdbcUrl())
                .isEqualTo("jdbc:postgresql://ep-algo-pooler.us-east-2.aws.neon.tech:5432/uber?sslmode=require");
        assertThat(result.username()).isEqualTo("neondb_owner");
        assertThat(result.password()).isEqualTo("secreto");
    }

    @Test
    void parse_channelBindingNoSoportadoPorJdbc_loDescarta() {
        DatabaseUrl result = DatabaseUrlConfig.parse(
                "postgresql://u:p@host/uber?sslmode=require&channel_binding=require");

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://host:5432/uber?sslmode=require");
    }

    @Test
    void parse_soloChannelBinding_noDejaInterrogacionSuelta() {
        DatabaseUrl result = DatabaseUrlConfig.parse("postgresql://u:p@host/uber?channel_binding=require");

        assertThat(result.jdbcUrl()).isEqualTo("jdbc:postgresql://host:5432/uber");
    }
}
