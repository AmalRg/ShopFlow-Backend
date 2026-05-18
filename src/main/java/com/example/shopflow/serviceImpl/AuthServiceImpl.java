package com.example.shopflow.serviceImpl;

import com.example.shopflow.dto.request.LoginRequest;
import com.example.shopflow.dto.request.RegisterRequest;
import com.example.shopflow.dto.response.AuthResponse;
import com.example.shopflow.entity.Cart;
import com.example.shopflow.entity.SellerProfile;
import com.example.shopflow.entity.User;
import com.example.shopflow.enums.Role;
import com.example.shopflow.exception.BusinessException;
import com.example.shopflow.exception.ResourceNotFoundException;
import com.example.shopflow.mapper.UserMapper;
import com.example.shopflow.repository.CartRepository;
import com.example.shopflow.repository.UserRepository;
import com.example.shopflow.security.JwtUtil;
import com.example.shopflow.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository,
                           CartRepository cartRepository,
                           PasswordEncoder passwordEncoder, UserMapper userMapper,
                           AuthenticationManager authManager,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.cartRepository = cartRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.authManager = authManager;
        this.jwtUtil = jwtUtil;
    }
 @Override
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail()))
            throw new BusinessException("Email déjà utilisé : " + req.getEmail());

        if (req.getRole() == Role.ADMIN)
            throw new BusinessException("Impossible de créer un compte ADMIN via l'inscription");

        User user = userMapper.toEntity(req);
        userRepository.save(user);

        if (user.getRole() == Role.CUSTOMER) {
            Cart cart = new Cart();
            cart.setCustomer(user);
            cartRepository.save(cart);
        }

        if (user.getRole() == Role.SELLER && req.getNomBoutique() != null) {
            SellerProfile profile = new SellerProfile();
            profile.setUser(user);
            profile.setNomBoutique(req.getNomBoutique());
            profile.setDescription(req.getDescriptionBoutique());
            user.setSellerProfile(profile);
            userRepository.save(user);
        }

        return buildAuthResponse(user);
    }
    @Override
    public AuthResponse login(LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.getEmail(), req.getPassword()));

        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));

        if (!user.isActif())
            throw new BusinessException("Compte désactivé");

        return buildAuthResponse(user);
    }
    @Override
    public AuthResponse refresh(String refreshToken) {
        String email = jwtUtil.extractUsername(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé"));
        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
                .accessToken(jwtUtil.generateAccessToken(user))
                .refreshToken(jwtUtil.generateRefreshToken(user))
                .userId(user.getId())
                .email(user.getEmail())
                .prenom(user.getPrenom())
                .nom(user.getNom())
                .role(user.getRole())
                .build();
    }
}