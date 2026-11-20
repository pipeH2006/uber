package com.uber.usuarios.application.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.util.Arrays;
import java.util.stream.Collectors;

@Configuration
public class DatabaseUrlConfig {
    @Bean
    public DataSource dataSource(DataSourceProperties properties) {
        String url = properties.getUrl();
        if (url != null && (url.startsWith("postgres://") || url.startsWith("postgresql://"))) {
            DatabaseUrl parsed = parse(url);
            HikariDataSource ds = new HikariDataSource();
            ds.setJdbcUrl(parsed.jdbcUrl());
            ds.setUsername(parsed.username());
            ds.setPassword(parsed.password());
            ds.setDriverClassName("org.postgresql.Driver");
            return ds;
        }
        return properties.initializeDataSourceBuilder().build();
    }

    static DatabaseUrl parse(String url) {
        URI uri = URI.create(url);
        String host = uri.getHost();
        int port = uri.getPort() > 0 ? uri.getPort() : 5432;
        String path = (uri.getPath() == null || uri.getPath().isBlank()) ? "" : uri.getPath();
        String query = cleanQuery(uri.getRawQuery());
        String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path + (query.isEmpty() ? "" : "?" + query);

        String username = "";
        String password = "";
        String userInfo = uri.getUserInfo();
        if (userInfo != null) {
            int idx = userInfo.indexOf(':');
            if (idx >= 0) {
                username = userInfo.substring(0, idx);
                password = userInfo.substring(idx + 1);
            } else {
                username = userInfo;
            }
        }
        return new DatabaseUrl(jdbcUrl, username, password);
    }

    private static String cleanQuery(String rawQuery) {
        if (rawQuery == null || rawQuery.isBlank()) {
            return "";
        }
        return Arrays.stream(rawQuery.split("&"))
                .filter(param -> !param.isBlank() && !param.startsWith("channel_binding="))
                .collect(Collectors.joining("&"));
    }

    public record DatabaseUrl(String jdbcUrl, String username, String password) {
    }
}
