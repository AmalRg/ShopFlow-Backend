package com.example.shopflow.dto.response;

import com.example.shopflow.enums.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileResponse {
    private Long          id;
    private String        prenom;
    private String        nom;
    private String        email;
    private Role          role;
    private boolean       actif;
    private LocalDateTime dateCreation;
}