package com.example.shopflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.example.shopflow.dto.request.CartItemRequest;
import com.example.shopflow.dto.response.CartResponseDto;
import com.example.shopflow.service.CartService;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasRole('CUSTOMER')")
@Tag(name = "Panier")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Voir mon panier")
    public ResponseEntity<CartResponseDto> getCart(Authentication auth) {
        return ResponseEntity.ok(cartService.getCart(auth.getName()));
    }

    @PostMapping("/items")
    @Operation(summary = "Ajouter un article")
    public ResponseEntity<CartResponseDto> addItem(
            @Valid @RequestBody CartItemRequest req,
            Authentication auth) {
        return ResponseEntity.ok(cartService.addItem(auth.getName(), req));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Modifier la quantité")
    public ResponseEntity<CartResponseDto> updateItem(
            @PathVariable Long itemId,
            @RequestParam int quantite,
            Authentication auth) {
        return ResponseEntity.ok(
                cartService.updateItem(auth.getName(), itemId, quantite));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Retirer un article")
    public ResponseEntity<CartResponseDto> removeItem(
            @PathVariable Long itemId,
            Authentication auth) {
        return ResponseEntity.ok(cartService.removeItem(auth.getName(), itemId));
    }

    @PostMapping("/coupon")
    @Operation(summary = "Appliquer un coupon")
    public ResponseEntity<CartResponseDto> applyCoupon(
            @RequestBody Map<String, String> body,
            Authentication auth) {
        return ResponseEntity.ok(
                cartService.applyCoupon(auth.getName(), body.get("code")));
    }

    @DeleteMapping("/coupon")
    @Operation(summary = "Retirer le coupon")
    public ResponseEntity<CartResponseDto> removeCoupon(Authentication auth) {
        return ResponseEntity.ok(cartService.removeCoupon(auth.getName()));
    }
}