package com.example.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ex: "Taille", "Couleur"
    @Column(nullable = false)
    private String attribut;

    // ex: "M", "Rouge"
    @Column(nullable = false)
    private String valeur;

    @Builder.Default
    private Integer stockSupplementaire = 0;

    // Delta de prix par rapport au produit parent (peut être négatif)
    @Builder.Default
    private BigDecimal prixDelta = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}