package com.example.salarytracker.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.stereotype.Component;

import com.example.salarytracker.entity.Role;
import com.example.salarytracker.repository.UserRepository;


@Component
public class CustomJwtAuthenticationConverter extends JwtAuthenticationConverter {

    private final UserRepository userRepository;

    public CustomJwtAuthenticationConverter(UserRepository userRepository) {
        this.userRepository = userRepository;

        // Set custom authority converter
        this.setJwtGrantedAuthoritiesConverter(this::convertJwtToAuthorities);
    }

   
    protected Collection<GrantedAuthority> convertJwtToAuthorities(Jwt jwt) {
        String email = jwt.getClaimAsString("email"); // Extract email from Google ID token
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Fetch user from DB and assign roles as Spring authorities
        userRepository.findByEmail(email).ifPresent(user -> {
        	System.out.println("email: "+email+" role: "+user.getRoles());
            for (Role role : user.getRoles()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
            }
        });

        return authorities;
    }
}

