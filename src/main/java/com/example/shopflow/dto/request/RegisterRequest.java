package com.example.shopflow.dto.request;

import jakarta.validation.constraints.*;
import com.example.shopflow.enums.Role;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank
    private String prenom;

    @NotBlank
    private String nom;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    private Role role = Role.CUSTOMER;

    private String nomBoutique;
    private String descriptionBoutique;
}