package com.example.salarytracker.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.salarytracker.entity.Role;
import com.example.salarytracker.entity.User;
import com.example.salarytracker.repository.RoleRepository;
import com.example.salarytracker.repository.UserRepository;


@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(UserRepository userRepository, RoleRepository roleRepository) {
        return args -> {
            
        	// Create roles
            Role userRole = roleRepository.findByName("USER")
                    .orElseGet(() -> roleRepository.save(new Role(null, "USER")));
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> roleRepository.save(new Role(null, "ADMIN")));

            // Create users
            if(userRepository.findByEmail("ajayachaurasia@gmail.com").isEmpty()) {
                User user = new User();
                user.setEmail("ajayachaurasia@gmail.com");
                user.getRoles().add(adminRole);
                userRepository.save(user);
            }

            if(userRepository.findByEmail("ajch212@gmail.com").isEmpty()) {
                User user = new User();
                user.setEmail("ajch212@gmail.com");
                user.getRoles().add(userRole);
                userRepository.save(user);
            }
        };
    }
}
