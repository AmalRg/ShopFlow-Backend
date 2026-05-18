package com.example.shopflow.controller;

import com.example.shopflow.dto.request.LoginRequest;
import com.example.shopflow.dto.request.RegisterRequest;
import com.example.shopflow.dto.response.AuthResponse;
import com.example.shopflow.enums.Role;
import com.example.shopflow.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@DisplayName("Tests AuthController")
class AuthControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    // ✅ Nouveau depuis Spring Boot 3.4+
    @MockitoBean AuthService authService;

    @Test
    @DisplayName("✅ POST /api/auth/login retourne 200")
    void login_Returns200() throws Exception {
        AuthResponse response = new AuthResponse();
        response.setAccessToken("access_token");
        response.setRefreshToken("refresh_token");
        response.setEmail("admin@shopflow.tn");
        response.setRole(Role.ADMIN);

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        LoginRequest request = new LoginRequest();
        request.setEmail("admin@shopflow.tn");
        request.setPassword("admin123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access_token"))
                .andExpect(jsonPath("$.email").value("admin@shopflow.tn"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @DisplayName("✅ POST /api/auth/register retourne 201")
    void register_Returns201() throws Exception {
        AuthResponse response = new AuthResponse();
        response.setAccessToken("token");
        response.setEmail("new@test.tn");
        response.setRole(Role.CUSTOMER);

        when(authService.register(any(RegisterRequest.class))).thenReturn(response);

        RegisterRequest request = new RegisterRequest();
        request.setPrenom("Ahmed");
        request.setNom("Test");
        request.setEmail("new@test.tn");
        request.setPassword("ahmed123");
        request.setRole(Role.CUSTOMER);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("new@test.tn"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }
}