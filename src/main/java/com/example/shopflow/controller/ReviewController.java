package com.example.shopflow.controller;

import com.example.shopflow.dto.request.ReviewRequest;
import com.example.shopflow.dto.response.ReviewResponse;
import com.example.shopflow.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Avis")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Poster un avis",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewResponse> create(
            @Valid @RequestBody ReviewRequest req,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.create(auth.getName(), req));
    }

    @GetMapping("/product/{productId}")
    @Operation(summary = "Avis d'un produit (approuvés)")
    public ResponseEntity<List<ReviewResponse>> getByProduct(
            @PathVariable Long productId) {
        return ResponseEntity.ok(reviewService.getByProduct(productId));
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Avis en attente (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ReviewResponse>> getPending() {
        return ResponseEntity.ok(reviewService.getAllPending());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Tous les avis (ADMIN)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<ReviewResponse>> getAll() {
        return ResponseEntity.ok(reviewService.getAll());
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Approuver un avis",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ReviewResponse> approve(@PathVariable Long id) {
        return ResponseEntity.ok(reviewService.approve(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un avis",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reviewService.delete(id);
        return ResponseEntity.noContent().build();
    }
}