package com.example.shopflow.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    @NotNull private Long addressId;
}