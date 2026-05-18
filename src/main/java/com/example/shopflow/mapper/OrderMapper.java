package com.example.shopflow.mapper;

import com.example.shopflow.dto.response.OrderResponse;
import com.example.shopflow.entity.Address;
import com.example.shopflow.entity.Order;
import com.example.shopflow.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponse toDto(Order o) {
        return OrderResponse.builder()
                .id(o.getId())
                .numeroCommande(o.getNumeroCommande())
                .statut(o.getStatut())
                .sousTotal(o.getSousTotal())
                .fraisLivraison(o.getFraisLivraison())
                .totalTTC(o.getTotalTTC())
                .dateCommande(o.getDateCommande().toString())
                .customerNom(o.getCustomer().getPrenom() + " " + o.getCustomer().getNom())
                .adresseLivraison(formatAddress(o.getAdresseLivraison()))
                .lignes(o.getLignes().stream()
                        .map(this::toItemDto)
                        .toList())
                .build();
    }

    private OrderResponse.OrderItemResponse toItemDto(OrderItem oi) {
        return OrderResponse.OrderItemResponse.builder()
                .productId(oi.getProduct().getId())
                .productNom(oi.getProduct().getNom())
                .quantite(oi.getQuantite())
                .prixUnitaire(oi.getPrixUnitaire())
                .build();
    }

    private String formatAddress(Address addr) {
        if (addr == null) return "";
        return addr.getRue() + ", " + addr.getVille() + ", " + addr.getPays();
    }
}