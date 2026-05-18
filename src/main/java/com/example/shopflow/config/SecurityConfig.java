package com.example.shopflow.config;

import com.example.shopflow.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtAuthFilter jwtAuthFilter,
            AuthenticationProvider authenticationProvider
    ) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // On s'assure que CORS est traité avant Security
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth

                        // ── 1. Routes Publiques (Priorité Haute) ──────────
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/reviews/product/**").permitAll()

                        // Swagger & Consoles
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers("/h2-console/**").permitAll()

                        // ── 2. Panier (IMPORTANT : Placé avant les règles génériques) ──
                        // On autorise CUSTOMER à faire du POST/GET/DELETE sur le panier
                        .requestMatchers("/api/cart/**").hasRole("CUSTOMER")

                        // ── 3. Rôles Spécifiques (ADMIN) ──────────────────
                        .requestMatchers("/api/users/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/reviews/pending").hasRole("ADMIN")
                        .requestMatchers("/api/dashboard/admin").hasRole("ADMIN")
                        .requestMatchers("/api/coupons/admin/**").hasRole("ADMIN")

                        // ── 4. Rôles Mixtes (SELLER + ADMIN) ──────────────
                        .requestMatchers("/api/dashboard/seller").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("SELLER")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasAnyRole("SELLER", "ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("SELLER")

                        // ── 5. Commandes ──────────────────────────────────
                        .requestMatchers(HttpMethod.GET, "/api/orders/my").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.POST, "/api/orders/**").hasRole("CUSTOMER")
                        .requestMatchers(HttpMethod.GET, "/api/orders/**").hasAnyRole("ADMIN", "SELLER")
                        .requestMatchers(HttpMethod.PUT, "/api/orders/**").hasAnyRole("ADMIN", "SELLER", "CUSTOMER")

                        // ── 6. Avis (Post par Customer) ───────────────────
                        .requestMatchers(HttpMethod.POST, "/api/reviews").authenticated()
                        .requestMatchers("/api/reviews").hasRole("ADMIN")

                        // ── 7. Profil & Reste ─────────────────────────────
                        .requestMatchers("/api/users/me/**").authenticated()
                        .anyRequest().authenticated()
                )
                // Nécessaire pour la console H2
                .headers(h -> h.frameOptions(f -> f.sameOrigin()))
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // On autorise explicitement ton port Angular
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }
}