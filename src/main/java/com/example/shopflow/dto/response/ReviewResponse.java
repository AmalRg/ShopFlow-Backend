package com.example.shopflow.dto.response;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ReviewResponse {
    private Long          id;
    private Integer       note;
    private String        commentaire;
    private String dateCreation;
    private boolean       approuve;
    private String        customerNom;
    private Long          customerId;
    private Long          productId;
    private String        productNom;
}