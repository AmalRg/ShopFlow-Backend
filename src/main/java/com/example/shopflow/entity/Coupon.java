package com.example.shopflow.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.example.shopflow.enums.CouponType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "coupons")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CouponType type;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valeur;

    @Column(nullable = false)
    private LocalDate dateExpiration;

    @Min(1)
    private Integer usagesMax;

    @Builder.Default
    private Integer usagesActuels = 0;

    @Builder.Default
    private boolean actif = true;

    public boolean isValide() {
        return actif
                && LocalDate.now().isBefore(dateExpiration)
                && (usagesMax == null || usagesActuels < usagesMax);
    }
}