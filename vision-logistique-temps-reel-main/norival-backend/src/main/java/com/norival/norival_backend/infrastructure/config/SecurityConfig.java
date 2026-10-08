package com.norival.norival_backend.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final com.norival.norival_backend.infrastructure.security.JwtTokenProvider tokenProvider;

    public SecurityConfig(com.norival.norival_backend.infrastructure.security.JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/ws/**", "/api/optimization/**", "/api/ai/**", "/api/rotations/**", "/api/dispatch/**", "/uploads/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(new com.norival.norival_backend.infrastructure.security.JwtAuthenticationFilter(tokenProvider), 
                             UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
