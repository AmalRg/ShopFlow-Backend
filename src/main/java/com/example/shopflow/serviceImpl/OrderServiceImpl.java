package com.example.shopflow.serviceImpl;

import com.example.shopflow.dto.request.OrderRequest;
import com.example.shopflow.dto.response.OrderResponse;
import com.example.shopflow.entity.*;
import com.example.shopflow.enums.OrderStatus;
import com.example.shopflow.exception.BusinessException;
import com.example.shopflow.exception.ResourceNotFoundException;
import com.example.shopflow.mapper.OrderMapper;
import com.example.shopflow.repository.CartRepository;
import com.example.shopflow.repository.OrderRepository;
import com.example.shopflow.repository.UserRepository;
import com.example.shopflow.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl  implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository  cartRepository;
    private final UserRepository  userRepository;
    private final OrderMapper     orderMapper;

    @Override
    public OrderResponse createFromCart(String email, OrderRequest req) {
        User customer = findUserByEmail(email);

        Cart cart = cartRepository.findByCustomerId(customer.getId())
                .orElseThrow(() -> new BusinessException("Panier introuvable"));

        if (cart.getLignes().isEmpty())
            throw new BusinessException("Le panier est vide");

        decrementStock(cart);

        Address address = customer.getAddresses().stream()
                .filter(a -> a.getId().equals(req.getAddressId()))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Adresse non trouvée"));

        BigDecimal sousTotal = calculateSousTotal(cart);
        BigDecimal frais     = calculateFrais(sousTotal);

        Order order = Order.builder()
                .customer(customer)
                .adresseLivraison(address)
                .sousTotal(sousTotal)
                .fraisLivraison(frais)
                .totalTTC(sousTotal.add(frais))
                .build();

        List<OrderItem> items = cart.getLignes().stream()
                .map(ci -> OrderItem.builder()
                        .order(order)
                        .product(ci.getProduct())
                        .variant(ci.getVariant())
                        .quantite(ci.getQuantite())
                        .prixUnitaire(ci.getProduct().getPrix())
                        .build())
                .toList();
        order.getLignes().addAll(items);

        clearCart(cart);

        return orderMapper.toDto(orderRepository.save(order));
    }
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getMyOrders(String email) {
        User customer = findUserByEmail(email);
        return orderRepository
                .findByCustomerIdOrderByDateCommandeDesc(customer.getId())
                .stream()
                .map(orderMapper::toDto)
                .toList();
    }
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getById(Long id) {
        return orderMapper.toDto(findOrderById(id));
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponse updateStatus(Long id, OrderStatus newStatus) {
        Order order = findOrderById(id);
        order.setStatut(newStatus);
        return orderMapper.toDto(orderRepository.save(order));
    }
    @Override
    public OrderResponse cancel(Long id, String email) {
        Order order = findOrderById(id);

        if (!order.getCustomer().getEmail().equals(email))
            throw new BusinessException("Non autorisé");

        if (order.getStatut() != OrderStatus.PENDING
                && order.getStatut() != OrderStatus.PAID)
            throw new BusinessException(
                    "Impossible d'annuler une commande au statut : "
                            + order.getStatut());

        order.getLignes().forEach(oi ->
                oi.getProduct().setStock(
                        oi.getProduct().getStock() + oi.getQuantite()));

        order.setStatut(OrderStatus.CANCELLED);
        return orderMapper.toDto(orderRepository.save(order));
    }
    @Override
    public List<OrderResponse> getByCustomer(String email) {
        return getMyOrders(email);
    }

    // ── Helpers ───────────────────────────────────────────────

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur non trouvé"));
    }

    private Order findOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commande non trouvée : " + id));
    }

    private void decrementStock(Cart cart) {
        cart.getLignes().forEach(item -> {
            Product p = item.getProduct();
            if (p.getStock() < item.getQuantite())
                throw new BusinessException(
                        "Stock insuffisant pour : " + p.getNom());
            p.setStock(p.getStock() - item.getQuantite());
        });
    }

    private BigDecimal calculateSousTotal(Cart cart) {
        return cart.getLignes().stream()
                .map(i -> i.getProduct().getPrix()
                        .multiply(BigDecimal.valueOf(i.getQuantite())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateFrais(BigDecimal sousTotal) {
        return sousTotal.compareTo(BigDecimal.valueOf(100)) >= 0
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(7);
    }

    private void clearCart(Cart cart) {
        cart.getLignes().clear();
        if (cart.getCoupon() != null) {
            cart.getCoupon().setUsagesActuels(
                    cart.getCoupon().getUsagesActuels() + 1);
            cart.setCoupon(null);
        }
        cartRepository.save(cart);
    }
}