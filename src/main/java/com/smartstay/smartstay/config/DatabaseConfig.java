package com.smartstay.smartstay.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url:}")
    private String url;

    @Value("${spring.datasource.username:}")
    private String username;

    @Value("${spring.datasource.password:}")
    private String password;

    @Bean
    @Primary
    public DataSource dataSource() throws URISyntaxException {
        // If url is Render / Heroku postgres:// or postgresql:// format, convert to JDBC
        if (url != null && !url.isBlank() && (url.startsWith("postgres://") || url.startsWith("postgresql://"))) {
            String sanitized = url.startsWith("postgresql://") ? url.replaceFirst("postgresql://", "postgres://") : url;
            URI uri = new URI(sanitized);
            String host = uri.getHost();
            int port = uri.getPort() == -1 ? 5432 : uri.getPort();
            String path = uri.getPath();
            String userInfo = uri.getUserInfo();
            String user = username;
            String pass = password;

            if (userInfo != null && userInfo.contains(":")) {
                String[] parts = userInfo.split(":", 2);
                user = parts[0];
                pass = parts[1];
            }

            String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(user);
            config.setPassword(pass);
            config.setDriverClassName("org.postgresql.Driver");
            return new HikariDataSource(config);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url != null && !url.isBlank() ? url : "jdbc:mysql://localhost:3306/smartstay_db");
        config.setUsername(username != null && !username.isBlank() ? username : "root");
        config.setPassword(password != null ? password : "");
        return new HikariDataSource(config);
    }
}
