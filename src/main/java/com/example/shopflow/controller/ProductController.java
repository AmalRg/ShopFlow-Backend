package com.example.shopflow.controller;

import com.example.shopflow.dto.request.ProductRequest;
import com.example.shopflow.dto.response.ProductResponse;
import com.example.shopflow.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Produits")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @Operation(summary = "Liste paginée avec filtres")
    public ResponseEntity<Page<ProductResponse>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) BigDecimal minPrix,
            @RequestParam(required = false) BigDecimal maxPrix,
            @RequestParam(required = false) Long sellerId,
            @RequestParam(defaultValue = "false") boolean promoOnly,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "dateCreation") String sortBy) {
        return ResponseEntity.ok(productService.getAll(
                categoryId, minPrix, maxPrix, sellerId, promoOnly, page, size, sortBy));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'un produit")
    public ResponseEntity<ProductResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Recherche full-text")
    public ResponseEntity<Page<ProductResponse>> search(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(productService.search(q, page, size));
    }

    @GetMapping("/top-selling")
    @Operation(summary = "Top 10 meilleures ventes")
    public ResponseEntity<List<ProductResponse>> topSelling() {
        return ResponseEntity.ok(productService.topSelling());
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Créer un produit",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest req,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(req, auth.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Modifier un produit",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest req,
            Authentication auth) {
        return ResponseEntity.ok(productService.update(id, req, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Désactiver un produit (soft delete)",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}