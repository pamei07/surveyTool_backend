package iks.surveytool.config;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Need to extract method into own class to avoid cycle in dependencies
@Configuration
public class KeycloakResolverConfig {
    // Use Spring Boot property file config instead of default keycloak.json
    @Bean
    public org.keycloak.adapters.KeycloakConfigResolver keycloakConfigResolver() {
        return new KeycloakSpringBootConfigResolver();
    }
}
