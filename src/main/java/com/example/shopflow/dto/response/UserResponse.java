package com.example.shopflow.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.example.shopflow.enums.Role;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String prenom;
    private String nom;
    private String email;
    private Role role;
    private boolean actif;
    private LocalDateTime dateCreation;
}