package com.example.shopflow.controller;

import com.example.shopflow.entity.ProductVariant;
import com.example.shopflow.service.ProductVariantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/variants")
@RequiredArgsConstructor
@Tag(name = "Variantes Produit")
@SecurityRequirement(name = "bearerAuth")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    @GetMapping
    @Operation(summary = "Lister les variantes d'un produit")
    public ResponseEntity<List<ProductVariant>> getVariants(
            @PathVariable Long productId) {
        return ResponseEntity.ok(productVariantService.getVariants(productId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Ajouter une variante")
    public ResponseEntity<ProductVariant> addVariant(
            @PathVariable Long productId,
            @RequestBody ProductVariant variant) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productVariantService.addVariant(productId, variant));
    }

    @DeleteMapping("/{variantId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Supprimer une variante")
    public ResponseEntity<Void> deleteVariant(
            @PathVariable Long productId,
            @PathVariable Long variantId) {
        productVariantService.deleteVariant(productId, variantId);
        return ResponseEntity.noContent().build();
    }
}