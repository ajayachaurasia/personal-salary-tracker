package com.example.salarytracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.example.salarytracker.util.CustomJwtAuthenticationConverter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
	
	 private final CustomJwtAuthenticationConverter jwtAuthenticationConverter;
	 
	 public SecurityConfig(CustomJwtAuthenticationConverter jwtAuthenticationConverter) {
	        this.jwtAuthenticationConverter = jwtAuthenticationConverter;
	 }
	     

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2Login()  // for UI/browser flow
            .and()
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter))); // for API/JWT validation
        return http.build();
    }
    
    
}