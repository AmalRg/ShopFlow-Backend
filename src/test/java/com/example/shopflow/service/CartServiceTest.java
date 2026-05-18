package com.example.shopflow.service;

import com.example.shopflow.dto.request.CartItemRequest;
import com.example.shopflow.entity.*;
import com.example.shopflow.enums.Role;
import com.example.shopflow.exception.BusinessException;
import com.example.shopflow.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@DisplayName("Tests CartService")
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CouponRepository couponRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User customer;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(1L);
        customer.setEmail("customer@test.tn");
        customer.setRole(Role.CUSTOMER);
        customer.setActif(true);

        cart = new Cart();
        cart.setId(1L);
        cart.setCustomer(customer);

        product = new Product();
        product.setId(1L);
        product.setNom("iPhone 15 Pro");
        product.setPrix(new BigDecimal("3499.00"));
        product.setStock(50);
        product.setActif(true);
    }

    @Test
    @DisplayName("✅ Voir panier vide")
    void getCart_Empty() {
        when(userRepository.findByEmail("customer@test.tn"))
                .thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(1L))
                .thenReturn(Optional.of(cart));

        Map<String, Object> result = (Map<String, Object>) cartService.getCart("customer@test.tn");

        assertNotNull(result);
        assertEquals(0, (int) result.get("items"));
        assertEquals(BigDecimal.ZERO, result.get("sousTotal"));
    }

    @Test
    @DisplayName("✅ Ajouter article au panier")
    void addItem_Success() {
        CartItemRequest req = new CartItemRequest();
        req.setProductId(1L);
        req.setQuantite(2);

        when(userRepository.findByEmail("customer@test.tn"))
                .thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(1L))
                .thenReturn(Optional.of(cart));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));
        when(cartRepository.save(any())).thenReturn(cart);

        Map<String, Object> result = (Map<String, Object>) cartService.addItem(
                "customer@test.tn", req);

        assertNotNull(result);
        verify(cartRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("❌ Ajouter article — stock insuffisant")
    void addItem_InsufficientStock() {
        product.setStock(1);
        CartItemRequest req = new CartItemRequest();
        req.setProductId(1L);
        req.setQuantite(10);

        when(userRepository.findByEmail("customer@test.tn"))
                .thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(1L))
                .thenReturn(Optional.of(cart));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> cartService.addItem("customer@test.tn", req));

        assertTrue(ex.getMessage().contains("Stock insuffisant"));
    }

    @Test
    @DisplayName("❌ Ajouter article — produit inactif")
    void addItem_ProductInactive() {
        product.setActif(false);
        CartItemRequest req = new CartItemRequest();
        req.setProductId(1L);
        req.setQuantite(1);

        when(userRepository.findByEmail("customer@test.tn"))
                .thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(1L))
                .thenReturn(Optional.of(cart));
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> cartService.addItem("customer@test.tn", req));

        assertEquals("Produit non disponible", ex.getMessage());
    }

    @Test
    @DisplayName("✅ Appliquer coupon valide")
    void applyCoupon_Valid() {
        Coupon coupon = new Coupon();
        coupon.setCode("GLID2025");
        coupon.setActif(true);
        coupon.setDateExpiration(
                java.time.LocalDate.now().plusMonths(6));
        coupon.setUsagesMax(100);
        coupon.setUsagesActuels(0);

        when(userRepository.findByEmail("customer@test.tn"))
                .thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(1L))
                .thenReturn(Optional.of(cart));
        when(couponRepository.findByCode("GLID2025"))
                .thenReturn(Optional.of(coupon));
        when(cartRepository.save(any())).thenReturn(cart);

        Map<String, Object> result = (Map<String, Object>) cartService.applyCoupon(
                "customer@test.tn", "GLID2025");

        assertNotNull(result);
        assertEquals("GLID2025", result.get("coupon"));
    }

    @Test
    @DisplayName("❌ Appliquer coupon invalide")
    void applyCoupon_Invalid() {
        Coupon coupon = new Coupon();
        coupon.setCode("EXPIRED");
        coupon.setActif(false);
        coupon.setDateExpiration(
                java.time.LocalDate.now().minusDays(1));

        when(userRepository.findByEmail("customer@test.tn"))
                .thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(1L))
                .thenReturn(Optional.of(cart));
        when(couponRepository.findByCode("EXPIRED"))
                .thenReturn(Optional.of(coupon));

        assertThrows(BusinessException.class,
                () -> cartService.applyCoupon("customer@test.tn", "EXPIRED"));
    }
}