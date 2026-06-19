package com.vote.common.config;

import com.vote.common.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
    this.jwtFilter = jwtFilter;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/**").permitAll()
        .requestMatchers(HttpMethod.GET, "/api/items/**").permitAll()
        .requestMatchers(HttpMethod.POST, "/api/votes/**").authenticated()
        .requestMatchers(HttpMethod.POST, "/api/items/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.PUT, "/api/items/**").hasRole("ADMIN")
        .requestMatchers(HttpMethod.DELETE, "/api/items/**").hasRole("ADMIN")
        .anyRequest().authenticated()
      )
      .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
      .exceptionHandling(ex -> ex
        .authenticationEntryPoint((request, response, authException) -> {
          response.setContentType("application/json;charset=UTF-8");
          response.setStatus(HttpStatus.UNAUTHORIZED.value());
          response.getWriter().write("{\"success\":false,\"message\":\"未授權，請先登入\"}");
        })
        .accessDeniedHandler((request, response, accessDeniedException) -> {
          response.setContentType("application/json;charset=UTF-8");
          response.setStatus(HttpStatus.FORBIDDEN.value());
          response.getWriter().write("{\"success\":false,\"message\":\"權限不足，僅管理員可執行此操作\"}");
        })
      );

    return http.build();
  }
}
