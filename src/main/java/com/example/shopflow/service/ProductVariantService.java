package com.example.shopflow.service;


import com.example.shopflow.entity.ProductVariant;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional

public interface ProductVariantService {




    List<ProductVariant> getVariants(Long productId) ;
    ProductVariant addVariant(Long productId, ProductVariant variant) ;
    void deleteVariant(Long productId, Long variantId) ;


}