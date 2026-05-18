package com.example.shopflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopflow.entity.ProductVariant;
import java.util.List;

public interface ProductVariantRepository
        extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductId(Long productId);
}