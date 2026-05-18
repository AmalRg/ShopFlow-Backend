package com.example.shopflow.mapper;

import com.example.shopflow.dto.request.ProductRequest;
import com.example.shopflow.dto.response.ProductResponse;
import com.example.shopflow.entity.Category;
import com.example.shopflow.entity.Product;
import com.example.shopflow.entity.User;
import com.example.shopflow.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductMapper {

    private final ReviewRepository reviewRepository;

    public ProductResponse toDto(Product p) {
        Double avg = reviewRepository.averageNoteByProductId(p.getId());
        return ProductResponse.builder()
                .id(p.getId())
                .nom(p.getNom())
                .description(p.getDescription())
                .prix(p.getPrix())
                .prixPromo(p.getPrixPromo())
                .stock(p.getStock())
                .actif(p.isActif())
                .enPromotion(p.getPrixPromo() != null)
                .imageUrl(p.getImageUrl())
                .dateCreation(p.getDateCreation().toString())
                .sellerNom(p.getSeller().getPrenom() + " " + p.getSeller().getNom())
                .sellerId(p.getSeller().getId())
                .categories(p.getCategories().stream()
                        .map(Category::getNom)
                        .collect(Collectors.toList()))
                .noteMoyenne(avg != null ? avg : 0.0)
                .nombreAvis(p.getReviews() != null ? p.getReviews().size() : 0)
                .build();
    }

    public Product toEntity(ProductRequest req, User seller) {
        return Product.builder()
                .nom(req.getNom())
                .description(req.getDescription())
                .prix(req.getPrix() != null
                        ? BigDecimal.valueOf(req.getPrix())
                        : BigDecimal.ZERO)
                .prixPromo(req.getPrixPromo() != null
                        ? BigDecimal.valueOf(req.getPrixPromo())
                        : null)
                .stock(req.getStock())
                .imageUrl(req.getImageUrl())
                .seller(seller)
                .build();
    }
}