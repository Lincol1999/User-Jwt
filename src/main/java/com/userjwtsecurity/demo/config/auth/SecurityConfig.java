package com.userjwtsecurity.demo.config.auth;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.userjwtsecurity.demo.config.auth.filters.JwtAuthenticacionFilter;
import com.userjwtsecurity.demo.config.auth.filters.JwtValidationFilter;

@Configuration
public class SecurityConfig {

        @Autowired
        private AuthenticationConfiguration authenticationConfiguration;

        @Bean
        BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        AuthenticationManager authenticationManager() throws Exception {
                return authenticationConfiguration.getAuthenticationManager();
        }

        @Bean
        SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
                return httpSecurity
                                .authorizeHttpRequests((authRoles) -> authRoles
                                                .antMatchers(HttpMethod.GET, "/api/users/v1").permitAll()
                                                .antMatchers(HttpMethod.GET, "/api/users/v1/{id}")
                                                .hasAnyRole("USER", "ADMIN", "SUPERVISOR")
                                                .antMatchers(HttpMethod.POST, "/api/users/v1").hasRole("ADMIN")
                                                .antMatchers("/api/users/v1/**").hasRole("ADMIN")
                                                // .antMatchers(HttpMethod.PUT, "/api/users/v1/{id}").permitAll()
                                                // .antMatchers(HttpMethod.DELETE, "/api/users/v1/{id}").permitAll()
                                                .anyRequest().authenticated())
                                .addFilter(new JwtAuthenticacionFilter(
                                                authenticationConfiguration.getAuthenticationManager()))
                                .addFilter(new JwtValidationFilter(
                                                authenticationConfiguration.getAuthenticationManager()))
                                .csrf((csrf) -> csrf.disable())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .cors(c -> c.configurationSource(corsConfigurationSource()))
                                .build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
                config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
                config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
                config.setAllowCredentials(true);

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", config);

                return source;
        }

        // Agregamos un bean de filtro con prioridad para que se aplique la
        // configuracion registerCorsConfiguration line 130.
        @Bean
        FilterRegistrationBean<CorsFilter> corsFilter() {
                // Con el new FilterRegistrationBean<CorsFilter> registramos el bean de
                // corsConfigurationSource
                FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<CorsFilter>(
                                new CorsFilter(corsConfigurationSource()));
                // Y damos un alta prioridad a este bean, y le damos alta preferencia con
                // HIGHEST_PRECEDENCE
                bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
                return bean;
        }
}
