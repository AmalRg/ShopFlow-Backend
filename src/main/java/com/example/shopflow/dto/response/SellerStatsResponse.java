package com.example.shopflow.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class SellerStatsResponse {
    private BigDecimal revenue;
    private long       commandesEnAttente;
    private int        mesProduits;
}