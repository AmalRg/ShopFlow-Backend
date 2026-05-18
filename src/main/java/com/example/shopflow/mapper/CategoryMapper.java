package com.example.shopflow.mapper;

import com.example.shopflow.controller.CategoryController;
import com.example.shopflow.dto.request.CategoryRequest;
import com.example.shopflow.dto.response.CategoryResponse;
import com.example.shopflow.entity.Category;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategoryMapper {

    public CategoryResponse toDto(Category c) {
        CategoryResponse r = new CategoryResponse();
        r.setId(c.getId());
        r.setNom(c.getNom());
        r.setDescription(c.getDescription());
        r.setParentId(c.getParentId());
        r.setParentNom(c.getParentNom());
        r.setSousCategories(
                c.getSousCategories() == null ? List.of()
                        : c.getSousCategories().stream()
                        .map(this::toDto)
                        .toList()
        );
        return r;
    }

    public Category toEntity(CategoryRequest req) {
        Category cat = new Category();
        cat.setNom(req.getNom());
        cat.setDescription(req.getDescription());
        if (req.getParentId() != null) {
            Category parent = new Category();
            parent.setId(req.getParentId());
            cat.setParent(parent);
        }
        return cat;
    }
}