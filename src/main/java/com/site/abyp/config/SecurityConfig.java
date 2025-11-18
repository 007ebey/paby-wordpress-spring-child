package com.site.abyp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Authorize requests
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/",
                                "/api/schema",
                                "/api/schema/**",
                                "/index",           // removed .html because URL paths usually don't include file extensions
                                "/css/**",
                                "/js/**",
                                "/login",           // allow access to login page itself
                                "/error"            // optionally allow error page
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                // Form login configuration
                .formLogin(form -> form
                        .loginPage("/login")              // your custom login page (must be permitted above)
                        .defaultSuccessUrl("/home", true) // force redirect after login
                        .permitAll()
                )
                // Logout configuration
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/index")
                        .permitAll()
                );

        // Optional: disable CSRF for testing, but keep enabled in prod
        // http.csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
