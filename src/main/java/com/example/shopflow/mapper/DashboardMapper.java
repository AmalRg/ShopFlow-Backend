package com.example.shopflow.mapper;

import com.example.shopflow.dto.response.AdminStatsResponse;
import com.example.shopflow.dto.response.SellerStatsResponse;
import com.example.shopflow.dto.response.TopProduitResponse;
import com.example.shopflow.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class DashboardMapper {

    public AdminStatsResponse toAdminStats(BigDecimal chiffreAffaires,
                                           long totalCommandes,
                                           long totalProduits,
                                           long totalUtilisateurs,
                                           List<Product> topProduits) {
        return AdminStatsResponse.builder()
                .chiffreAffaires(chiffreAffaires)
                .totalCommandes(totalCommandes)
                .totalProduits(totalProduits)
                .totalUtilisateurs(totalUtilisateurs)
                .topProduits(topProduits.stream()
                        .map(this::toTopProduit)
                        .toList())
                .build();
    }

    public SellerStatsResponse toSellerStats(BigDecimal revenue,
                                             long commandesEnAttente,
                                             int mesProduits) {
        return SellerStatsResponse.builder()
                .revenue(revenue)
                .commandesEnAttente(commandesEnAttente)
                .mesProduits(mesProduits)
                .build();
    }

    private TopProduitResponse toTopProduit(Product p) {
        return TopProduitResponse.builder()
                .id(p.getId())
                .nom(p.getNom())
                .prix(p.getPrix())
                .stock(p.getStock())
                .actif(p.isActif())
                .build();
    }
}