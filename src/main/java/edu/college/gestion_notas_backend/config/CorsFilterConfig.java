package edu.college.gestion_notas_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Configuración adicional de CORS usando Filter
 * Útil para casos donde necesitas más control sobre el CORS
 */
@Configuration
public class CorsFilterConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Configuración para desarrollo - React con Vite
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
        
        // Para producción, especifica los dominios exactos:
        // configuration.setAllowedOrigins(Arrays.asList(
        //     "http://localhost:3000",           // React dev server
        //     "http://localhost:4200",           // Angular dev server  
        //     "http://localhost:8080",           // Vue dev server
        //     "https://tu-frontend-app.com"      // Dominio de producción
        // ));
        
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        // Headers adicionales que puedes necesitar
        configuration.setExposedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type", 
            "Accept",
            "X-Requested-With",
            "Cache-Control",
            "X-Total-Count",
            "X-Total-Pages"
        ));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return new CorsFilter(source);
    }
}