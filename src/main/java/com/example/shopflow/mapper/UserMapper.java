package com.example.shopflow.mapper;

import com.example.shopflow.dto.request.RegisterRequest;
import com.example.shopflow.dto.response.AddressResponse;
import com.example.shopflow.dto.response.UserProfileResponse;
import com.example.shopflow.dto.response.UserResponse;
import com.example.shopflow.entity.Address;
import com.example.shopflow.entity.User;
import com.example.shopflow.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final PasswordEncoder passwordEncoder;

    public UserMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User toEntity(RegisterRequest req) {
        User user = new User();
        user.setPrenom(req.getPrenom());
        user.setNom(req.getNom());
        user.setEmail(req.getEmail());
        user.setMotDePasse(passwordEncoder.encode(req.getPassword()));
        user.setRole(req.getRole() != null ? req.getRole() : Role.CUSTOMER);
        user.setActif(true);
        return user;
    }
    public UserResponse toResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setPrenom(user.getPrenom());
        response.setNom(user.getNom());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        return response;
    }
    public AddressResponse toAddressDto(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .rue(a.getRue())
                .ville(a.getVille())
                .codePostal(a.getCodePostal())
                .pays(a.getPays())
                .principal(a.isPrincipal())
                .build();
    }
    public UserProfileResponse toDto(User u) {
        return UserProfileResponse.builder()
                .id(u.getId())
                .prenom(u.getPrenom())
                .nom(u.getNom())
                .email(u.getEmail())
                .role(u.getRole())
                .actif(u.isActif())
                .dateCreation(u.getDateCreation())
                .build();
    }
}