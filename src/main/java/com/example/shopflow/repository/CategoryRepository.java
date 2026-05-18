package com.example.shopflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopflow.entity.Category;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentIsNull();
    Optional<Category> findByNom(String nom);
    boolean existsByNom(String nom);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM product_categories WHERE categories_id = :id", nativeQuery = true)
    void deleteProductAssociations(Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE categories SET parent_id = NULL WHERE parent_id = :id", nativeQuery = true)
    void detachChildrenNative(Long id);
}