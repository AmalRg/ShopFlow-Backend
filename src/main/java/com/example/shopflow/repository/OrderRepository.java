package com.example.shopflow.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import com.example.shopflow.entity.Order;
import com.example.shopflow.enums.OrderStatus;
import java.math.BigDecimal;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIdOrderByDateCommandeDesc(Long customerId);
    List<Order> findByStatut(OrderStatus statut);

    // Dashboard admin : chiffre d'affaires global
    @Query("SELECT COALESCE(SUM(o.totalTTC), 0) FROM Order o WHERE o.statut != 'CANCELLED'")
    BigDecimal totalRevenue();

    // Dashboard seller
    @Query("SELECT COALESCE(SUM(o.totalTTC), 0) FROM Order o " +
            "JOIN o.lignes oi WHERE oi.product.seller.id = :sellerId " +
            "AND o.statut != 'CANCELLED'")
    BigDecimal totalRevenueBySeller(@Param("sellerId") Long sellerId);

    @Query("SELECT COUNT(o) FROM Order o JOIN o.lignes oi " +
            "WHERE oi.product.seller.id = :sellerId AND o.statut = 'PENDING'")
    Long countPendingBySeller(@Param("sellerId") Long sellerId);

    // Vérifier si un customer a acheté un produit (pour les avis)
    @Query("SELECT COUNT(o) > 0 FROM Order o JOIN o.lignes oi " +
            "WHERE o.customer.id = :customerId AND oi.product.id = :productId " +
            "AND o.statut IN ('DELIVERED', 'SHIPPED')")
    boolean customerHasPurchasedProduct(
            @Param("customerId") Long customerId,
            @Param("productId") Long productId);
}