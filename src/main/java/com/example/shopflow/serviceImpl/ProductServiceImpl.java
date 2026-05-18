package com.example.shopflow.serviceImpl;

import com.example.shopflow.dto.request.ProductRequest;
import com.example.shopflow.dto.response.ProductResponse;
import com.example.shopflow.entity.Category;
import com.example.shopflow.entity.Product;
import com.example.shopflow.entity.User;
import com.example.shopflow.exception.ResourceNotFoundException;
import com.example.shopflow.mapper.ProductMapper;
import com.example.shopflow.repository.CategoryRepository;
import com.example.shopflow.repository.ProductRepository;
import com.example.shopflow.repository.UserRepository;
import com.example.shopflow.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl  implements ProductService {

    private final ProductRepository  productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository     userRepository;
    private final ProductMapper      productMapper;

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> getAll(Long categoryId, BigDecimal minPrix,
                                        BigDecimal maxPrix, Long sellerId,
                                        boolean promoOnly, int page, int size,
                                        String sortBy) {
        String criteria = (sortBy == null || sortBy.isBlank()
                || sortBy.equals("dateCreation")) ? "id" : sortBy;

        Sort sort = "prix".equalsIgnoreCase(criteria)
                ? Sort.by(Sort.Direction.ASC, criteria)
                : Sort.by(Sort.Direction.DESC, criteria);

        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository
                .findWithFilters(categoryId, minPrix, maxPrix, sellerId, promoOnly, pageable)
                .map(productMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return productMapper.toDto(findProductById(id));
    }
    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String q, int page, int size) {
        return productRepository
                .searchByQuery(q, PageRequest.of(page, size))
                .map(productMapper::toDto);
    }
    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> topSelling() {
        return productRepository.findTopSelling(PageRequest.of(0, 10))
                .stream()
                .map(productMapper::toDto)
                .toList();
    }
    @Override
    public ProductResponse create(ProductRequest req, String sellerEmail) {
        User seller = userRepository.findByEmail(sellerEmail)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vendeur non trouvé"));

        List<Category> categories = categoryRepository.findAllById(
                req.getCategoryIds() != null ? req.getCategoryIds() : List.of());

        Product product = productMapper.toEntity(req, seller);
        product.setCategories(new HashSet<>(categories));

        return productMapper.toDto(productRepository.save(product));
    }
    @Override
    public ProductResponse update(Long id, ProductRequest req, String email) {
        Product product = findProductById(id);

        product.setNom(req.getNom());
        product.setDescription(req.getDescription());
        product.setPrix(req.getPrix() != null
                ? BigDecimal.valueOf(req.getPrix())
                : product.getPrix());
        product.setPrixPromo(req.getPrixPromo() != null
                ? BigDecimal.valueOf(req.getPrixPromo())
                : null);
        product.setStock(req.getStock());
        product.setImageUrl(req.getImageUrl());

        if (req.getCategoryIds() != null) {
            product.getCategories().clear();
            product.getCategories().addAll(
                    categoryRepository.findAllById(req.getCategoryIds()));
        }

        return productMapper.toDto(productRepository.save(product));
    }
    @Override
    public void delete(Long id) {
        Product product = findProductById(id);
        product.setActif(false);
        productRepository.save(product);
    }

    // ── Helper ───────────────────────────────────────────────

    private Product findProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Produit non trouvé : " + id));
    }
}