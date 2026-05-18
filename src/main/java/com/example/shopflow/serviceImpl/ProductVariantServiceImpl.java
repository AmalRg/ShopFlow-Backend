package com.example.shopflow.serviceImpl;

import com.example.shopflow.entity.Product;
import com.example.shopflow.entity.ProductVariant;
import com.example.shopflow.exception.ResourceNotFoundException;
import com.example.shopflow.repository.ProductRepository;
import com.example.shopflow.repository.ProductVariantRepository;
import com.example.shopflow.service.ProductVariantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductRepository        productRepository;
    private final ProductVariantRepository variantRepository;
    @Override
    @Transactional(readOnly = true)
    public List<ProductVariant> getVariants(Long productId) {
        return findProductById(productId).getVariants();
    }

    @Override
    public ProductVariant addVariant(Long productId, ProductVariant variant) {
        Product product = findProductById(productId);
        variant.setProduct(product);
        product.getVariants().add(variant);
        productRepository.save(product);
        return variant;
    }
    @Override
    public void deleteVariant(Long productId, Long variantId) {
        Product product = findProductById(productId);
        product.getVariants().removeIf(v -> v.getId().equals(variantId));
        productRepository.save(product);
    }

    // ── Helper ───────────────────────────────────────────────

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit non trouvé : " + productId));
    }
}