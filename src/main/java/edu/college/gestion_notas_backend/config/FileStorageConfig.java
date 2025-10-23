package edu.college.gestion_notas_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "file")
@Data
public class FileStorageConfig {
    private String uploadDir;
}