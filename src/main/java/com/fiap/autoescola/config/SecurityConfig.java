package com.fiap.autoescola.config;

import com.fiap.autoescola.security.CustomUserDetailsService;
import com.fiap.autoescola.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @org.springframework.beans.factory.annotation.Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource(){
        var config=new org.springframework.web.cors.CorsConfiguration();
        config.setAllowedOrigins(java.util.Arrays.asList(allowedOrigins));
        config.setAllowedMethods(java.util.List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("Authorization","Content-Type","Accept"));
        config.setExposedHeaders(java.util.List.of("Location"));config.setMaxAge(3600L);
        var source=new org.springframework.web.cors.UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",config);return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .exceptionHandling(errors -> errors
                .authenticationEntryPoint((request,response,exception) -> {
                    response.setStatus(401);response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"status\":401,\"erro\":\"Autenticação necessária ou token inválido\"}");
                })
                .accessDeniedHandler((request,response,exception) -> {
                    response.setStatus(403);response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"status\":403,\"erro\":\"Acesso negado\"}");
                }))
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
