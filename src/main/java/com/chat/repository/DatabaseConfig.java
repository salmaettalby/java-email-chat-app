package com.chat.repository;

import com.chat.repository.ChatMessageRepository;
import com.chat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.boot.jdbc.DataSourceBuilder;

import javax.sql.DataSource;

@Configuration
public class DatabaseConfig {
    
    @Value("${chat.database.type:sqlite}")
    private String databaseType;
    
    @Value("${spring.datasource.url:jdbc:sqlite:chat.db}")
    private String datasourceUrl;

    @Bean
    @Primary
    public DataSource dataSource() {
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create();
        
        if ("sqlite".equalsIgnoreCase(databaseType)) {
            // Use SQLite configuration
            dataSourceBuilder.driverClassName("org.sqlite.JDBC");
            dataSourceBuilder.url(datasourceUrl);
            System.out.println("Using SQLite database: " + datasourceUrl);
            return dataSourceBuilder.build();
        }
        
        // Fallback to SQLite
        System.out.println("Database type not properly configured, falling back to SQLite");
        dataSourceBuilder.driverClassName("org.sqlite.JDBC");
        dataSourceBuilder.url("jdbc:sqlite:chat.db");
        return dataSourceBuilder.build();
    }

    @Bean
    public UserRepository userRepository() {
        return new UserRepository();
    }

    @Bean
    public ChatMessageRepository chatMessageRepository() {
        return new ChatMessageRepository();
    }
}