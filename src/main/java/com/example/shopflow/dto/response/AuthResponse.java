package com.example.shopflow.dto.response;

import lombok.*;
import com.example.shopflow.enums.Role;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long userId;
    private String email;
    private String prenom;
    private String nom;
    private Role role;
}