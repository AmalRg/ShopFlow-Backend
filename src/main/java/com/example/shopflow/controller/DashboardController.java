package com.example.shopflow.controller;

import com.example.shopflow.dto.response.AdminStatsResponse;
import com.example.shopflow.dto.response.SellerStatsResponse;
import com.example.shopflow.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
@SecurityRequirement(name = "bearerAuth")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Stats admin")
    public ResponseEntity<AdminStatsResponse> getAdminStats() {
        return ResponseEntity.ok(dashboardService.getAdminStats());
    }

    @GetMapping("/seller")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    @Operation(summary = "Stats vendeur")
    public ResponseEntity<SellerStatsResponse> getSellerStats(Authentication auth) {
        return ResponseEntity.ok(dashboardService.getSellerStats(auth.getName()));
    }
}