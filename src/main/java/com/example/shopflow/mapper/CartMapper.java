package com.example.shopflow.mapper;

import com.example.shopflow.dto.response.CartResponseDto;
import com.example.shopflow.entity.Cart;
import com.example.shopflow.entity.CartItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
public class CartMapper {

    public CartResponseDto toDto(Cart cart, BigDecimal sousTotal,
                                 BigDecimal remise, BigDecimal frais,
                                 String couponCode) {
        return CartResponseDto.builder()
                .cartId(cart.getId())
                .items(cart.getLignes().size())
                .lignes(cart.getLignes().stream()
                        .map(this::toItemDto)
                        .collect(Collectors.toList()))
                .sousTotal(sousTotal)
                .remise(remise)
                .fraisLivraison(frais)
                .total(sousTotal.subtract(remise).add(frais))
                .coupon(couponCode)
                .build();
    }

    private CartResponseDto.CartItemDto toItemDto(CartItem item) {
        BigDecimal prix = item.getProduct().isEnPromotion()
                ? item.getProduct().getPrixPromo()
                : item.getProduct().getPrix();

        return CartResponseDto.CartItemDto.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productNom(item.getProduct().getNom())
                .productImageUrl(item.getProduct().getImageUrl())
                .prix(prix)
                .quantite(item.getQuantite())
                .build();
    }
}