package com.example.shopflow.controller;

import com.example.shopflow.dto.request.CategoryRequest;
import com.example.shopflow.dto.response.CategoryResponse;
import com.example.shopflow.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Catégories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Arbre des catégories")
    public ResponseEntity<List<CategoryResponse>> getAll() {
        return ResponseEntity.ok(categoryService.getAll());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer une catégorie",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryResponse> create(@RequestBody CategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryService.create(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier une catégorie",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<CategoryResponse> update(
            @PathVariable Long id,
            @RequestBody CategoryRequest req) {
        return ResponseEntity.ok(categoryService.update(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer une catégorie",
            security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}

//package com.example.shopflow.controller;
//
//import com.example.shopflow.dto.response.OrderResponse;
//import com.example.shopflow.enums.OrderStatus;
//import com.example.shopflow.service.OrderService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.Data;
//import org.springframework.http.*;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//import com.example.shopflow.dto.response.CategoryResponse;
//import com.example.shopflow.entity.Category;
//import com.example.shopflow.service.CategoryService;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/api/categories")
//@Tag(name = "Catégories")
//public class CategoryController {
//
//    private final CategoryService categoryService;
//    private final OrderService orderService;
//
//    public CategoryController(CategoryService categoryService, OrderService orderService) {
//        this.categoryService = categoryService;
//        this.orderService = orderService;
//    }
//
//    @GetMapping
//    @Operation(summary = "Arbre des catégories")
//    public ResponseEntity<List<CategoryResponse>> getAll() {
//        return ResponseEntity.ok(
//                categoryService.getAll().stream()
//                        .map(this::toResponse).toList()
//        );
//    }
//
//    @PostMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Créer une catégorie",
//            security = @SecurityRequirement(name = "bearerAuth"))
//    public ResponseEntity<CategoryResponse> create(
//            @RequestBody CategoryRequest req) {
//        Category cat = new Category();
//        cat.setNom(req.getNom());
//        cat.setDescription(req.getDescription());
//        if (req.getParentId() != null) {
//            Category parent = new Category();
//            parent.setId(req.getParentId());
//            cat.setParent(parent);
//        }
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(toResponse(categoryService.create(cat)));
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Modifier une catégorie",
//            security = @SecurityRequirement(name = "bearerAuth"))
//    public ResponseEntity<CategoryResponse> update(
//            @PathVariable Long id,
//            @RequestBody CategoryRequest req) {
//        Category cat = new Category();
//        cat.setNom(req.getNom());
//        cat.setDescription(req.getDescription());
//        if (req.getParentId() != null) {
//            Category parent = new Category();
//            parent.setId(req.getParentId());
//            cat.setParent(parent);
//        }
//        return ResponseEntity.ok(
//                toResponse(categoryService.update(id, cat)));
//    }
//
//    @PutMapping("/{id}/status")
//    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
//    @Operation(summary = "Mettre à jour le statut")
//    public ResponseEntity<OrderResponse> updateStatus(
//            @PathVariable Long id,
//            @RequestBody Map<String, String> body) {
//        // Accepte {"statut": "SHIPPED"} au lieu de "SHIPPED"
//        String statusStr = body.get("statut");
//        if (statusStr == null) statusStr = body.get("status");
//        OrderStatus status = OrderStatus.valueOf(statusStr.toUpperCase());
//        return ResponseEntity.ok(orderService.updateStatus(id, status));
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Supprimer une catégorie",
//            security = @SecurityRequirement(name = "bearerAuth"))
//    public ResponseEntity<Void> delete(@PathVariable Long id) {
//        categoryService.delete(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    // ── DTOs ────────────────────────────────────────────────
//
//    @Data
//    public static class CategoryRequest {
//        private String nom;
//        private String description;
//        private Long   parentId;
//    }
//
//    private CategoryResponse toResponse(Category c) {
//        CategoryResponse r = new CategoryResponse();
//        r.setId(c.getId());
//        r.setNom(c.getNom());
//        r.setDescription(c.getDescription());
//        r.setParentId(c.getParentId());
//        r.setParentNom(c.getParentNom());
//        r.setSousCategories(
//                c.getSousCategories() == null ? List.of()
//                        : c.getSousCategories().stream()
//                        .map(this::toResponse).toList()
//        );
//        return r;
//    }
//}