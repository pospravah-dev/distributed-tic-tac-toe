package com.flamingo.tictactoe.gameengine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@EnableWebFluxSecurity
public class CorsConfig {
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)   // disable CSRF for API
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()               // allow API calls
                )
                .build();
    }
//    @Bean
//    public WebFluxConfigurer corsConfigurer() {
//        return new WebFluxConfigurer() {
//            @Override
//            public void addCorsMappings(CorsRegistry registry) {
//                registry.addMapping("/**")
//                    .allowedOrigins("http://localhost:3000")
//                    .allowedMethods("*")
//                    .allowedHeaders("*")
//                    .allowCredentials(true);
//            }
//        };
//    }
}
