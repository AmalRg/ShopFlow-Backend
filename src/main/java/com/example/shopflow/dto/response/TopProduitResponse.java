package com.example.shopflow.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class TopProduitResponse {
    private Long       id;
    private String     nom;
    private BigDecimal prix;
    private int        stock;
    private boolean    actif;
}