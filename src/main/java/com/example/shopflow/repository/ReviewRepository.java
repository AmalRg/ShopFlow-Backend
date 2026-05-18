package com.example.shopflow.repository;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import com.example.shopflow.entity.Review;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdAndApprouveTrue(Long productId);
    List<Review> findByApprouveFalse();
    List<Review> findByCustomerId(Long customerId);
    Optional<Review> findByCustomerIdAndProductId(Long customerId, Long productId);
    boolean existsByCustomerIdAndProductId(Long customerId, Long productId);

    @Query("SELECT AVG(r.note) FROM Review r " +
            "WHERE r.product.id = :productId AND r.approuve = true")
    Double averageNoteByProductId(@Param("productId") Long productId);

    @Query("SELECT r.product.nom, AVG(r.note), COUNT(r) " +
            "FROM Review r WHERE r.approuve = true " +
            "GROUP BY r.product ORDER BY AVG(r.note) DESC")
    List<Object[]> findTopRatedProducts();
}