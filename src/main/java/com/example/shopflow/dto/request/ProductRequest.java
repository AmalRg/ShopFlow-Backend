package com.example.shopflow.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class ProductRequest {
    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 3, message = "Minimum 3 caractères")
    private String nom;

    private String description;

    @NotNull(message = "Le prix est obligatoire")
    @DecimalMin(value = "0.01", message = "Prix doit être > 0")
    private Double prix;

    private Double prixPromo;

    @NotNull(message = "Le stock est obligatoire")
    @Min(value = 0, message = "Stock ne peut pas être négatif")
    private Integer stock;

    private String     imageUrl;
    private List<Long> categoryIds;
}