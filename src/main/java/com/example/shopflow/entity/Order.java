package com.example.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;
import com.example.shopflow.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroCommande;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus statut = OrderStatus.PENDING;

    @Column(precision = 10, scale = 2)
    private BigDecimal sousTotal;

    @Column(precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal fraisLivraison = new BigDecimal("7.00");

    @Column(precision = 10, scale = 2)
    private BigDecimal totalTTC;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime dateCommande = LocalDateTime.now();

    // Relations
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address adresseLivraison;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderItem> lignes = new ArrayList<>();

    @PrePersist
    public void generateOrderNumber() {
        if (this.numeroCommande == null) {
            this.numeroCommande = "ORD-" + LocalDateTime.now().getYear()
                    + "-" + String.format("%05d", (long)(Math.random() * 99999));
        }
    }
}