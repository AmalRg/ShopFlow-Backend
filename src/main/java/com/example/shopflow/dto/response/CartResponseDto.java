package com.example.shopflow.dto.response;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CartResponseDto {
    private Long   cartId;
    private int    items;
    private List<CartItemDto> lignes;
    private BigDecimal sousTotal;
    private BigDecimal remise;
    private BigDecimal fraisLivraison;
    private BigDecimal total;
    private String coupon;

    @Data
    @Builder
    public static class CartItemDto {
        private Long   id;
        private Long   productId;
        private String productNom;
        private String productImageUrl;
        private BigDecimal prix;
        private Integer quantite;
        private Long   variantId;
    }
}