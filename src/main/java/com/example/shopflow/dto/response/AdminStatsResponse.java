package com.example.shopflow.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class AdminStatsResponse {
    private BigDecimal      chiffreAffaires;
    private long            totalCommandes;
    private long            totalProduits;
    private long            totalUtilisateurs;
    private List<TopProduitResponse> topProduits;
}