package com.example.shopflow.service;

import com.example.shopflow.dto.request.OrderRequest;
import com.example.shopflow.dto.response.OrderResponse;
import com.example.shopflow.entity.*;
import com.example.shopflow.enums.*;
import com.example.shopflow.exception.BusinessException;
import com.example.shopflow.exception.ResourceNotFoundException;
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
@DisplayName("Tests OrderService")
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartRepository cartRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User customer;
    private Cart cart;
    private Product product;
    private Address address;
    private Order order;

    @BeforeEach
    void setUp() {
        customer = new User();
        customer.setId(1L);
        customer.setEmail("customer@test.tn");
        customer.setRole(Role.CUSTOMER);
        customer.setActif(true);

        address = new Address();
        address.setId(1L);
        address.setRue("12 Rue Liberté");
        address.setVille("Tunis");
        address.setCodePostal("1001");
        address.setPays("Tunisie");
        address.setUser(customer);
        customer.setAddresses(new ArrayList<>(List.of(address)));

        product = new Product();
        product.setId(1L);
        product.setNom("iPhone 15 Pro");
        product.setPrix(new BigDecimal("3499.00"));
        product.setStock(50);
        product.setActif(true);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantite(2);

        cart = new Cart();
        cart.setId(1L);
        cart.setCustomer(customer);
        cart.setLignes(new ArrayList<>(List.of(cartItem)));

        order = new Order();
        order.setId(1L);
        order.setNumeroCommande("ORD-2026-00001");
        order.setStatut(OrderStatus.PENDING);
        order.setCustomer(customer);
        order.setAdresseLivraison(address);
        order.setSousTotal(new BigDecimal("6998.00"));
        order.setFraisLivraison(BigDecimal.ZERO);
        order.setTotalTTC(new BigDecimal("6998.00"));
        order.setLignes(new ArrayList<>());
    }

    @Test
    @DisplayName("✅ Créer commande depuis panier")
    void createFromCart_Success() {
        OrderRequest req = new OrderRequest();
        req.setAddressId(1L);

        when(userRepository.findByEmail("customer@test.tn"))
                .thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(1L))
                .thenReturn(Optional.of(cart));
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(cartRepository.save(any())).thenReturn(cart);

        OrderResponse response = orderService.createFromCart(
                "customer@test.tn", req);

        assertNotNull(response);
        assertEquals("ORD-2026-00001", response.getNumeroCommande());
        assertEquals(OrderStatus.PENDING, response.getStatut());
        // Stock doit être décrémenté
        assertEquals(48, product.getStock());
    }

    @Test
    @DisplayName("❌ Panier vide — commande impossible")
    void createFromCart_EmptyCart() {
        cart.setLignes(new ArrayList<>());
        OrderRequest req = new OrderRequest();
        req.setAddressId(1L);

        when(userRepository.findByEmail("customer@test.tn"))
                .thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(1L))
                .thenReturn(Optional.of(cart));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createFromCart("customer@test.tn", req));

        assertEquals("Le panier est vide", ex.getMessage());
    }

    @Test
    @DisplayName("❌ Stock insuffisant au moment de la commande")
    void createFromCart_InsufficientStock() {
        product.setStock(1); // seulement 1 en stock mais quantité = 2
        OrderRequest req = new OrderRequest();
        req.setAddressId(1L);

        when(userRepository.findByEmail("customer@test.tn"))
                .thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomerId(1L))
                .thenReturn(Optional.of(cart));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.createFromCart("customer@test.tn", req));

        assertTrue(ex.getMessage().contains("Stock insuffisant"));
    }

    @Test
    @DisplayName("✅ Annuler commande PENDING")
    void cancel_PendingOrder_Success() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        OrderResponse response = orderService.cancel(1L, "customer@test.tn");

        assertEquals(OrderStatus.CANCELLED, response.getStatut());
    }

    @Test
    @DisplayName("❌ Annuler commande SHIPPED impossible")
    void cancel_ShippedOrder_Fails() {
        order.setStatut(OrderStatus.SHIPPED);
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> orderService.cancel(1L, "customer@test.tn"));

        assertTrue(ex.getMessage().contains("Impossible d'annuler"));
    }

    @Test
    @DisplayName("✅ Mettre à jour statut commande")
    void updateStatus_Success() {
        when(orderRepository.findById(1L))
                .thenReturn(Optional.of(order));
        when(orderRepository.save(any())).thenReturn(order);

        OrderResponse response = orderService.updateStatus(1L, OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, response.getStatut());
    }

    @Test
    @DisplayName("✅ Mes commandes retourne liste")
    void getMyOrders_ReturnsList() {
        when(userRepository.findByEmail("customer@test.tn"))
                .thenReturn(Optional.of(customer));
        when(orderRepository.findByCustomerIdOrderByDateCommandeDesc(1L))
                .thenReturn(List.of(order));

        List<OrderResponse> orders = orderService.getMyOrders("customer@test.tn");

        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
        assertEquals("ORD-2026-00001", orders.get(0).getNumeroCommande());
    }
}