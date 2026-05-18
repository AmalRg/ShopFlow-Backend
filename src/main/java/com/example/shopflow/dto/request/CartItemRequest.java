package com.example.shopflow.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
    @NotNull private Long productId;
    private Long variantId;
    @Min(1) private Integer quantite = 1;
}