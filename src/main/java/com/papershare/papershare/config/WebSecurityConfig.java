package com.papershare.papershare.config;

import com.papershare.papershare.model.User;
import com.papershare.papershare.model.Role;
import com.papershare.papershare.service.UserService;
import com.papershare.papershare.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Autowired
    UserService userService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserServiceImpl userService) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .anyRequest().permitAll() // Require authentication for other requests
                )
                .logout(LogoutConfigurer::permitAll);

        // Use UserServiceImpl to load users from database (replace with your implementation)
        http.userDetailsService(userService);

        return http.build();
    }

}