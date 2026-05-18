package com.example.shopflow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String nom;
    private String description;
    private Long parentId;
    private String parentNom;
    private List<CategoryResponse> sousCategories;
}