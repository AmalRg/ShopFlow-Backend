package com.example.shopflow.mapper;

import com.example.shopflow.dto.response.ReviewResponse;
import com.example.shopflow.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponse toDto(Review r) {
        return ReviewResponse.builder()
                .id(r.getId())
                .note(r.getNote())
                .commentaire(r.getCommentaire())
                .dateCreation(r.getDateCreation().toString())
                .approuve(r.isApprouve())
                .customerNom(r.getCustomer().getPrenom()
                        + " " + r.getCustomer().getNom())
                .customerId(r.getCustomer().getId())
                .productId(r.getProduct().getId())
                .productNom(r.getProduct().getNom())
                .build();
    }
}