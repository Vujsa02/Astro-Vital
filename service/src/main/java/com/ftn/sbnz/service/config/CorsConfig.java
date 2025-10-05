package com.ftn.sbnz.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    // Allow specific origins - frontend development server
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:8081",
        "http://127.0.0.1:8081",
        "http://localhost:3000", // Common React dev server port
        "http://127.0.0.1:3000"));

    // Allow all HTTP methods
    configuration.setAllowedMethods(Arrays.asList("*"));

    // Allow all headers
    configuration.setAllowedHeaders(Arrays.asList("*"));

    // Allow credentials (cookies, authorization headers, etc.)
    configuration.setAllowCredentials(true);

    // How long the browser should cache preflight request results
    configuration.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);

    return source;
  }

  @Bean
  public CorsFilter corsFilter() {
    return new CorsFilter(corsConfigurationSource());
  }
}