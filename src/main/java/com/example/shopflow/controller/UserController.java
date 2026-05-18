package com.example.shopflow.controller;

import com.example.shopflow.dto.request.AddressRequest;
import com.example.shopflow.dto.request.UpdateProfileRequest;
import com.example.shopflow.dto.response.AddressResponse;
import com.example.shopflow.dto.response.UserProfileResponse;
import com.example.shopflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "Utilisateurs & Adresses")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;

    // ── Profil ───────────────────────────────────────────────

    @GetMapping("/me")
    @Operation(summary = "Mon profil")
    public ResponseEntity<UserProfileResponse> getMe(Authentication auth) {
        return ResponseEntity.ok(userService.getMe(auth.getName()));
    }

    @PutMapping("/me")
    @Operation(summary = "Modifier mon profil")
    public ResponseEntity<UserProfileResponse> updateMe(
            @RequestBody UpdateProfileRequest req,
            Authentication auth) {
        return ResponseEntity.ok(userService.updateMe(auth.getName(), req));
    }

    // ── Adresses ─────────────────────────────────────────────

    @GetMapping("/me/addresses")
    @Operation(summary = "Mes adresses")
    public ResponseEntity<List<AddressResponse>> getAddresses(Authentication auth) {
        return ResponseEntity.ok(userService.getAddresses(auth.getName()));
    }

    @PostMapping("/me/addresses")
    @Operation(summary = "Ajouter une adresse")
    public ResponseEntity<AddressResponse> addAddress(
            @RequestBody AddressRequest req,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.addAddress(auth.getName(), req));
    }

    @DeleteMapping("/me/addresses/{addressId}")
    @Operation(summary = "Supprimer une adresse")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId,
            Authentication auth) {
        userService.deleteAddress(auth.getName(), addressId);
        return ResponseEntity.noContent().build();
    }

    // ── Admin ─────────────────────────────────────────────────

    @GetMapping("/admin/all")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tous les utilisateurs")
    public ResponseEntity<List<UserProfileResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PutMapping("/admin/{id}/toggle-active")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer / désactiver un compte")
    public ResponseEntity<UserProfileResponse> toggleActive(@PathVariable Long id) {
        return ResponseEntity.ok(userService.toggleActive(id));
    }
}