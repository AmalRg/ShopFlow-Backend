package com.example.shopflow.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.shopflow.entity.Coupon;
import com.example.shopflow.exception.ResourceNotFoundException;
import com.example.shopflow.repository.CouponRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons")
@SecurityRequirement(name = "bearerAuth")
public class CouponController {

    private final CouponRepository couponRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un coupon")
    public ResponseEntity<Coupon> create(@RequestBody Coupon coupon) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponRepository.save(coupon));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Modifier un coupon")
    public ResponseEntity<Coupon> update(@PathVariable Long id, @RequestBody Coupon req) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon non trouvé : " + id));
        coupon.setCode(req.getCode());
        coupon.setType(req.getType());
        coupon.setValeur(req.getValeur());
        coupon.setDateExpiration(req.getDateExpiration());
        coupon.setUsagesMax(req.getUsagesMax());
        coupon.setActif(req.isActif());
        return ResponseEntity.ok(couponRepository.save(coupon));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un coupon")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        couponRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/validate/{code}")
    @Operation(summary = "Vérifier un code promo")
    public ResponseEntity<Map<String, Object>> validate(@PathVariable String code) {
        java.util.Optional<com.example.shopflow.entity.Coupon> optional =
                couponRepository.findByCode(code);

        Map<String, Object> result = new HashMap<>();

        if (optional.isPresent()) {
            com.example.shopflow.entity.Coupon c = optional.get();
            result.put("valide", c.isValide());
            result.put("type", c.getType());
            result.put("valeur", c.getValeur());
        } else {
            result.put("valide", false);
            result.put("message", "Code promo introuvable");
        }

        return ResponseEntity.ok(result);
    }
}