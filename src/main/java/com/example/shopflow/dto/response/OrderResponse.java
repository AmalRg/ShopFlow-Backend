package com.example.shopflow.dto.response;

import lombok.*;
import com.example.shopflow.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String numeroCommande;
    private OrderStatus statut;
    private BigDecimal sousTotal;
    private BigDecimal fraisLivraison;
    private BigDecimal totalTTC;
    private String dateCommande;
    private String customerNom;
    private String adresseLivraison;
    private List<OrderItemResponse> lignes;

    @Data @Builder
    public static class OrderItemResponse {
        private Long productId;
        private String productNom;
        private Integer quantite;
        private BigDecimal prixUnitaire;
    }
}