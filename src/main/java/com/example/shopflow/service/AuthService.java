package com.example.shopflow.service;

import com.example.shopflow.mapper.UserMapper;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.shopflow.dto.request.*;
import com.example.shopflow.dto.response.AuthResponse;
import com.example.shopflow.entity.*;
import com.example.shopflow.enums.Role;
import com.example.shopflow.exception.*;
import com.example.shopflow.repository.*;
import com.example.shopflow.security.JwtUtil;

@Service
@Transactional
public interface AuthService {
    AuthResponse register(RegisterRequest req) ;
    AuthResponse login(LoginRequest req);
    AuthResponse refresh(String refreshToken) ;

}