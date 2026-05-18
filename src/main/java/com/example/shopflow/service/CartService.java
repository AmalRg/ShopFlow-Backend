
package com.example.shopflow.service;

import com.example.shopflow.dto.request.CartItemRequest;
import com.example.shopflow.dto.response.CartResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional

public interface CartService {


  CartResponseDto getCart(String email) ;

  CartResponseDto addItem(String email, CartItemRequest req) ;
  CartResponseDto updateItem(String email, Long itemId, int quantite) ;
  CartResponseDto removeItem(String email, Long itemId) ;
  CartResponseDto applyCoupon(String email, String code) ;
  CartResponseDto removeCoupon(String email);
}





//package com.example.shopflow.service;
//
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import com.example.shopflow.dto.request.CartItemRequest;
//import com.example.shopflow.dto.response.CartResponseDto;
//import com.example.shopflow.entity.*;
//import com.example.shopflow.exception.*;
//import com.example.shopflow.repository.*;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Transactional
//public class CartService {
//
//    private final CartRepository    cartRepository;
//    private final ProductRepository productRepository;
//    private final CouponRepository  couponRepository;
//    private final UserRepository    userRepository;
//
//    public CartService(CartRepository cartRepository,
//                       ProductRepository productRepository,
//                       CouponRepository couponRepository,
//                       UserRepository userRepository) {
//        this.cartRepository    = cartRepository;
//        this.productRepository = productRepository;
//        this.couponRepository  = couponRepository;
//        this.userRepository    = userRepository;
//    }
//
//    @Transactional(readOnly = true)
//    public CartResponseDto getCart(String email) {
//        Cart cart = getOrCreateCart(email);
//        return buildResponse(cart);
//    }
//
//    public CartResponseDto addItem(String email, CartItemRequest req) {
//        Cart cart = getOrCreateCart(email);
//
//        Product product = productRepository.findById(req.getProductId())
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Produit non trouvé : " + req.getProductId()));
//
//        if (!product.isActif())
//            throw new BusinessException("Produit non disponible");
//
//        if (product.getStock() < req.getQuantite())
//            throw new BusinessException(
//                    "Stock insuffisant. Disponible : " + product.getStock());
//
//        // Vérifier si déjà dans le panier
//        boolean found = false;
//        for (CartItem item : cart.getLignes()) {
//            if (item.getProduct().getId().equals(req.getProductId())
//                    && item.getVariant() == null
//                    && req.getVariantId() == null) {
//                item.setQuantite(item.getQuantite() + req.getQuantite());
//                found = true;
//                break;
//            }
//        }
//
//        if (!found) {
//            CartItem newItem = new CartItem();
//            newItem.setCart(cart);
//            newItem.setProduct(product);
//            newItem.setQuantite(req.getQuantite());
//            cart.getLignes().add(newItem);
//        }
//
//        return buildResponse(cartRepository.save(cart));
//    }
//
//    public CartResponseDto updateItem(String email, Long itemId, int quantite) {
//        Cart cart = getOrCreateCart(email);
//
//        CartItem item = cart.getLignes().stream()
//                .filter(i -> i.getId().equals(itemId))
//                .findFirst()
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Article non trouvé dans le panier"));
//
//        if (quantite <= 0) {
//            cart.getLignes().remove(item);
//        } else {
//            if (item.getProduct().getStock() < quantite)
//                throw new BusinessException("Stock insuffisant");
//            item.setQuantite(quantite);
//        }
//
//        return buildResponse(cartRepository.save(cart));
//    }
//
//    public CartResponseDto removeItem(String email, Long itemId) {
//        Cart cart = getOrCreateCart(email);
//        cart.getLignes().removeIf(i -> i.getId().equals(itemId));
//        return buildResponse(cartRepository.save(cart));
//    }
//
//    public CartResponseDto applyCoupon(String email, String code) {
//        Cart cart = getOrCreateCart(email);
//        Coupon coupon = couponRepository.findByCode(code)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Coupon non trouvé : " + code));
//        if (!coupon.isValide())
//            throw new BusinessException("Coupon invalide ou expiré");
//        cart.setCoupon(coupon);
//        return buildResponse(cartRepository.save(cart));
//    }
//
//    public CartResponseDto removeCoupon(String email) {
//        Cart cart = getOrCreateCart(email);
//        cart.setCoupon(null);
//        return buildResponse(cartRepository.save(cart));
//    }
//
//    // ── Helpers ──────────────────────────────────────────────
//
//    private Cart getOrCreateCart(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Utilisateur non trouvé"));
//        return cartRepository.findByCustomerId(user.getId())
//                .orElseGet(() -> {
//                    Cart c = new Cart();
//                    c.setCustomer(user);
//                    return cartRepository.save(c);
//                });
//    }
//
//    private CartResponseDto buildResponse(Cart cart) {
//        // Calculer sous-total
//        BigDecimal sousTotal = cart.getLignes().stream()
//                .map(i -> {
//                    BigDecimal prix = i.getProduct().isEnPromotion()
//                            ? i.getProduct().getPrixPromo()
//                            : i.getProduct().getPrix();
//                    return prix.multiply(BigDecimal.valueOf(i.getQuantite()));
//                })
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        // Calculer remise
//        BigDecimal remise = BigDecimal.ZERO;
//        String couponCode = null;
//        if (cart.getCoupon() != null && cart.getCoupon().isValide()) {
//            Coupon c = cart.getCoupon();
//            couponCode = c.getCode();
//            remise = switch (c.getType()) {
//                case PERCENT -> sousTotal
//                        .multiply(c.getValeur())
//                        .divide(BigDecimal.valueOf(100));
//                case FIXED   -> c.getValeur().min(sousTotal);
//            };
//        }
//
//        // Frais de livraison
//        BigDecimal frais = sousTotal.subtract(remise)
//                .compareTo(BigDecimal.valueOf(100)) >= 0
//                ? BigDecimal.ZERO
//                : BigDecimal.valueOf(7);
//
//        BigDecimal total = sousTotal.subtract(remise).add(frais);
//
//        // Construire les lignes DTO
//        List<CartResponseDto.CartItemDto> lignesDto = cart.getLignes().stream()
//                .map(item -> {
//                    BigDecimal prix = item.getProduct().isEnPromotion()
//                            ? item.getProduct().getPrixPromo()
//                            : item.getProduct().getPrix();
//                    return CartResponseDto.CartItemDto.builder()
//                            .id(item.getId())
//                            .productId(item.getProduct().getId())
//                            .productNom(item.getProduct().getNom())
//                            .productImageUrl(item.getProduct().getImageUrl())
//                            .prix(prix)
//                            .quantite(item.getQuantite())
//                            .build();
//                })
//                .collect(Collectors.toList());
//
//        return CartResponseDto.builder()
//                .cartId(cart.getId())
//                .items(cart.getLignes().size())
//                .lignes(lignesDto)
//                .sousTotal(sousTotal)
//                .remise(remise)
//                .fraisLivraison(frais)
//                .total(total)
//                .coupon(couponCode)
//                .build();
//    }
//}