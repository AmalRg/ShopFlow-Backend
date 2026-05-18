package com.example.shopflow.dto.response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductResponse {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prix;
    private BigDecimal prixPromo;
    private Integer stock;
    private boolean actif;
    private boolean enPromotion;
    private String imageUrl;
    private String dateCreation;
    private String sellerNom;
    private Long sellerId;
    private List<String> categories;
    private Double noteMoyenne;
    private Integer nombreAvis;
}