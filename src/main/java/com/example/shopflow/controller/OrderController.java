package com.example.shopflow.controller;

import com.example.shopflow.dto.request.OrderRequest;
import com.example.shopflow.dto.request.StatusRequest;
import com.example.shopflow.dto.response.OrderResponse;
import com.example.shopflow.enums.OrderStatus;
import com.example.shopflow.service.OrderService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Commandes")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(summary = "Toutes les commandes")
    public ResponseEntity<List<OrderResponse>> getAll() {
        return ResponseEntity.ok(orderService.getAll());
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Mes commandes")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getByCustomer(auth.getName()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Détail d'une commande")
    public ResponseEntity<OrderResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Passer une commande")
    public ResponseEntity<OrderResponse> create(
            @RequestBody OrderRequest req,
            Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createFromCart(auth.getName(), req));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN','SELLER')")
    @Operation(summary = "Changer le statut")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody StatusRequest req) {
        return ResponseEntity.ok(orderService.updateStatus(
                id, OrderStatus.valueOf(req.getStatut().toUpperCase())));
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasRole('CUSTOMER')")
    @Operation(summary = "Annuler une commande")
    public ResponseEntity<OrderResponse> cancel(
            @PathVariable Long id,
            Authentication auth) {
        return ResponseEntity.ok(orderService.cancel(id, auth.getName()));
    }




}