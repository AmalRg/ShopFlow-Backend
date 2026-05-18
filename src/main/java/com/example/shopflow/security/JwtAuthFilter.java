package com.example.shopflow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil            jwtUtil;
    private final UserDetailsService userDetailsService;

    // @Lazy pour éviter la dépendance circulaire
    public JwtAuthFilter(JwtUtil jwtUtil,
                         @Lazy UserDetailsService userDetailsService) {
        this.jwtUtil            = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain         filterChain
    ) throws ServletException, IOException {

        String path = request.getServletPath();

        // On ignore le filtre pour Swagger et H2
        if (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.startsWith("/h2-console")) {
            filterChain.doFilter(request, response);
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("Requête : " + request.getRequestURI() + " | Auth : " + SecurityContextHolder.getContext().getAuthentication());

            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        System.out.println("DEBUG - Header : " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("DEBUG - Filtre arrêté : Header absent ou mal formaté");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token    = authHeader.substring(7);
            final String email    = jwtUtil.extractUsername(token);
            if (email != null &&
                    SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(email);

                if (jwtUtil.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );
                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.warn("JWT invalide : " + e.getMessage());
        }

        System.out.println("Requête : " + request.getRequestURI() + " | Auth : " + SecurityContextHolder.getContext().getAuthentication());
        filterChain.doFilter(request, response);
    }
}