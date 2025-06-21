package com.shuzi.permissionservice.config;

import com.zaxxer.hikari.HikariDataSource;
import io.seata.rm.datasource.DataSourceProxy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * Ensure all JDBC operations are routed through Seata DataSourceProxy so that
 * permission-service can join global transactions and undo log is recorded.
 */
@Configuration
public class SeataDataSourceProxyConfig {

    @Bean("actualDataSource")
    public DataSource actualDataSource(DataSourceProperties properties) {
        // Let Spring Boot build the real HikariDataSource from properties
        return properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Primary
    @Bean
    public DataSource dataSource(@Qualifier("actualDataSource") DataSource dataSource) {
        return new DataSourceProxy(dataSource);
    }
} 