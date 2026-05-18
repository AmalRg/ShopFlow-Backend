package com.example.shopflow.repository;

import com.example.shopflow.entity.Product;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.actif = true AND (" +
            "LOWER(p.nom) LIKE LOWER(CONCAT('%', :q, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Product> searchByQuery(@Param("q") String q, Pageable pageable);

    @Query(value = """
        SELECT DISTINCT p FROM Product p 
        LEFT JOIN p.categories c 
        WHERE p.actif = true 
        AND (:categoryId IS NULL OR c.id = :categoryId) 
        AND (:minPrix IS NULL OR p.prix >= :minPrix) 
        AND (:maxPrix IS NULL OR p.prix <= :maxPrix) 
        AND (:sellerId IS NULL OR p.seller.id = :sellerId) 
        AND (:promoOnly = false OR p.prixPromo IS NOT NULL)
        """,
            countQuery = """
        SELECT COUNT(DISTINCT p) FROM Product p 
        LEFT JOIN p.categories c 
        WHERE p.actif = true 
        AND (:categoryId IS NULL OR c.id = :categoryId)
        """)
    Page<Product> findWithFilters(
            @Param("categoryId") Long categoryId,
            @Param("minPrix") BigDecimal minPrix,
            @Param("maxPrix") BigDecimal maxPrix,
            @Param("sellerId") Long sellerId,
            @Param("promoOnly") boolean promoOnly,
            Pageable pageable);

    @Query("SELECT p FROM Product p JOIN OrderItem oi ON oi.product = p " +
            "WHERE p.actif = true " +
            "GROUP BY p.id " +
            "ORDER BY SUM(oi.quantite) DESC")
    List<Product> findTopSelling(Pageable pageable);

    List<Product> findBySellerId(Long sellerId);
    List<Product> findByCategoriesId(Long categoryId);
}