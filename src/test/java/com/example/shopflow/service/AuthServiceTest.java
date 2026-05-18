package com.example.shopflow.service;

import com.example.shopflow.dto.request.LoginRequest;
import com.example.shopflow.dto.request.RegisterRequest;
import com.example.shopflow.dto.response.AuthResponse;
import com.example.shopflow.entity.Cart;
import com.example.shopflow.entity.User;
import com.example.shopflow.enums.Role;
import com.example.shopflow.exception.BusinessException;
import com.example.shopflow.repository.CartRepository;
import com.example.shopflow.repository.UserRepository;
import com.example.shopflow.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Tests AuthService")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CartRepository cartRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authManager;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private User mockUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setPrenom("Fatma");
        mockUser.setNom("Khalil");
        mockUser.setEmail("fatma@test.tn");
        mockUser.setMotDePasse("encodedPassword");
        mockUser.setRole(Role.CUSTOMER);
        mockUser.setActif(true);

        registerRequest = new RegisterRequest();
        registerRequest.setPrenom("Fatma");
        registerRequest.setNom("Khalil");
        registerRequest.setEmail("fatma@test.tn");
        registerRequest.setPassword("fatma123");
        registerRequest.setRole(Role.CUSTOMER);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("fatma@test.tn");
        loginRequest.setPassword("fatma123");
    }

    // ── Register ──────────────────────────────────────────────

    @Test
    @DisplayName("✅ Inscription réussie d'un customer")
    void register_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(cartRepository.save(any(Cart.class))).thenReturn(new Cart());
        when(jwtUtil.generateAccessToken(any())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh_token");

        AuthResponse response = authService.register(registerRequest);

        assertNotNull(response);
        assertEquals("fatma@test.tn", response.getEmail());
        assertEquals(Role.CUSTOMER, response.getRole());
        assertEquals("access_token", response.getAccessToken());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("❌ Inscription échoue si email déjà utilisé")
    void register_EmailAlreadyExists() {
        when(userRepository.existsByEmail("fatma@test.tn")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.register(registerRequest));

        assertTrue(ex.getMessage().contains("Email déjà utilisé"));
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("❌ Inscription échoue si rôle ADMIN")
    void register_AdminRoleForbidden() {
        registerRequest.setRole(Role.ADMIN);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.register(registerRequest));

        assertTrue(ex.getMessage().contains("ADMIN"));
    }

    @Test
    @DisplayName("✅ Panier créé automatiquement pour CUSTOMER")
    void register_CartCreatedForCustomer() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(mockUser);
        when(cartRepository.save(any())).thenReturn(new Cart());
        when(jwtUtil.generateAccessToken(any())).thenReturn("token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh");

        authService.register(registerRequest);

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    @DisplayName("✅ Pas de panier créé pour SELLER")
    void register_NoCartCreatedForSeller() {
        registerRequest.setRole(Role.SELLER);
        User sellerUser = new User();
        sellerUser.setId(2L);
        sellerUser.setEmail("seller@test.tn");
        sellerUser.setRole(Role.SELLER);
        sellerUser.setActif(true);

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(sellerUser);
        when(jwtUtil.generateAccessToken(any())).thenReturn("token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh");

        authService.register(registerRequest);

        verify(cartRepository, never()).save(any());
    }

    // ── Login ─────────────────────────────────────────────────

    @Test
    @DisplayName("✅ Login réussi")
    void login_Success() {
        when(authManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("fatma@test.tn", "fatma123"));
        when(userRepository.findByEmail("fatma@test.tn"))
                .thenReturn(Optional.of(mockUser));
        when(jwtUtil.generateAccessToken(any())).thenReturn("access_token");
        when(jwtUtil.generateRefreshToken(any())).thenReturn("refresh_token");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("access_token", response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    @DisplayName("❌ Login échoue si compte désactivé")
    void login_AccountDisabled() {
        mockUser.setActif(false);
        when(authManager.authenticate(any())).thenReturn(
                new UsernamePasswordAuthenticationToken("fatma@test.tn", "fatma123"));
        when(userRepository.findByEmail(anyString()))
                .thenReturn(Optional.of(mockUser));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> authService.login(loginRequest));

        assertEquals("Compte désactivé", ex.getMessage());
    }

    @Test
    @DisplayName("❌ Login échoue avec mauvais mot de passe")
    void login_WrongPassword() {
        when(authManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));
    }
}