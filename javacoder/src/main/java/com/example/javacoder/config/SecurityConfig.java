package com.example.javacoder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            //csrf korumasini rest API icin kapattim.
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authz -> authz
                //public endpointlere herkes erisebilir.
                .requestMatchers("/api/code/health").permitAll()
                .requestMatchers("/api/code/languages").permitAll()
                .requestMatchers("/api/code/execute/**").permitAll()
                .requestMatchers("/api/visit").permitAll()
                //kalan endpointler icin ise authentication istenir.
                .anyRequest().authenticated()
            )
            //session yönetimi: STATELESS -> Session tutulmaz, her request bağımsızdır
            //stateful olsaydi her kullanici icin oturum olusturulurdu, kullanici state'i hafizada tutulurdu. server side-session storage
            //redis de kullanabilirdik.
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        //burada credential (or: authorization header, cookie) gonderimine izin verdik
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 