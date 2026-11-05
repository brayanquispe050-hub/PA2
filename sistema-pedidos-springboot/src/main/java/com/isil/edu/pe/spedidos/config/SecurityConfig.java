package com.isil.edu.pe.spedidos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/reportes/**", "/api/v1/pedidos/**").hasAnyRole("OPERADOR", "CONSULTA")
                        .requestMatchers(HttpMethod.POST, "/pedidos/**", "/api/v1/pedidos").hasRole("OPERADOR")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form.permitAll())
                .logout(logout -> logout.permitAll());

        return http.build();
    }
}
