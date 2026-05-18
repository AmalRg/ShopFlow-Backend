
package com.example.shopflow.serviceImpl;

import com.example.shopflow.dto.request.CartItemRequest;
import com.example.shopflow.dto.response.CartResponseDto;
import com.example.shopflow.entity.*;
import com.example.shopflow.exception.BusinessException;
import com.example.shopflow.exception.ResourceNotFoundException;
import com.example.shopflow.mapper.CartMapper;
import com.example.shopflow.repository.CartRepository;
import com.example.shopflow.repository.CouponRepository;
import com.example.shopflow.repository.ProductRepository;
import com.example.shopflow.repository.UserRepository;
import com.example.shopflow.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository    cartRepository;
    private final ProductRepository productRepository;
    private final CouponRepository  couponRepository;
    private final UserRepository    userRepository;
    private final CartMapper        cartMapper;

    // ── CRUD ─────────────────────────────────────────────────

    @Transactional(readOnly = true)
    @Override
    public CartResponseDto getCart(String email) {
        return buildResponse(getOrCreateCart(email));
    }
    @Override
    public CartResponseDto addItem(String email, CartItemRequest req) {
        Cart cart       = getOrCreateCart(email);
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit non trouvé : " + req.getProductId()));

        if (!product.isActif())
            throw new BusinessException("Produit non disponible");

        if (product.getStock() < req.getQuantite())
            throw new BusinessException(
                    "Stock insuffisant. Disponible : " + product.getStock());

        boolean found = false;
        for (CartItem item : cart.getLignes()) {
            if (item.getProduct().getId().equals(req.getProductId())
                    && item.getVariant() == null
                    && req.getVariantId() == null) {
                item.setQuantite(item.getQuantite() + req.getQuantite());
                found = true;
                break;
            }
        }

        if (!found) {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setQuantite(req.getQuantite());
            cart.getLignes().add(newItem);
        }

        return buildResponse(cartRepository.save(cart));
    }
    @Override
    public CartResponseDto updateItem(String email, Long itemId, int quantite) {
        Cart cart = getOrCreateCart(email);

        CartItem item = cart.getLignes().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Article non trouvé dans le panier"));

        if (quantite <= 0) {
            cart.getLignes().remove(item);
        } else {
            if (item.getProduct().getStock() < quantite)
                throw new BusinessException("Stock insuffisant");
            item.setQuantite(quantite);
        }

        return buildResponse(cartRepository.save(cart));
    }
    @Override
    public CartResponseDto removeItem(String email, Long itemId) {
        Cart cart = getOrCreateCart(email);
        cart.getLignes().removeIf(i -> i.getId().equals(itemId));
        return buildResponse(cartRepository.save(cart));
    }
    @Override
    public CartResponseDto applyCoupon(String email, String code) {
        Cart cart     = getOrCreateCart(email);
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Coupon non trouvé : " + code));

        if (!coupon.isValide())
            throw new BusinessException("Coupon invalide ou expiré");

        cart.setCoupon(coupon);
        return buildResponse(cartRepository.save(cart));
    }
    @Override
    public CartResponseDto removeCoupon(String email) {
        Cart cart = getOrCreateCart(email);
        cart.setCoupon(null);
        return buildResponse(cartRepository.save(cart));
    }

    // ── Helpers ───────────────────────────────────────────────

    private Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur non trouvé"));
        return cartRepository.findByCustomerId(user.getId())
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setCustomer(user);
                    return cartRepository.save(c);
                });
    }

    private CartResponseDto buildResponse(Cart cart) {
        BigDecimal sousTotal  = calculateSousTotal(cart);
        BigDecimal remise     = calculateRemise(cart, sousTotal);
        BigDecimal frais      = calculateFrais(sousTotal, remise);
        String     couponCode = cart.getCoupon() != null
                ? cart.getCoupon().getCode()
                : null;

        return cartMapper.toDto(cart, sousTotal, remise, frais, couponCode);
    }

    // ── Calculs ───────────────────────────────────────────────

    private BigDecimal calculateSousTotal(Cart cart) {
        return cart.getLignes().stream()
                .map(i -> {
                    BigDecimal prix = i.getProduct().isEnPromotion()
                            ? i.getProduct().getPrixPromo()
                            : i.getProduct().getPrix();
                    return prix.multiply(BigDecimal.valueOf(i.getQuantite()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateRemise(Cart cart, BigDecimal sousTotal) {
        if (cart.getCoupon() == null || !cart.getCoupon().isValide())
            return BigDecimal.ZERO;

        Coupon coupon = cart.getCoupon();
        return switch (coupon.getType()) {
            case PERCENT -> sousTotal
                    .multiply(coupon.getValeur())
                    .divide(BigDecimal.valueOf(100));
            case FIXED   -> coupon.getValeur().min(sousTotal);
        };
    }

    private BigDecimal calculateFrais(BigDecimal sousTotal, BigDecimal remise) {
        return sousTotal.subtract(remise).compareTo(BigDecimal.valueOf(100)) >= 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(7);
    }
}

