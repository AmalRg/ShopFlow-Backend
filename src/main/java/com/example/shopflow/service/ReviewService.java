package com.example.shopflow.service;

import com.example.shopflow.dto.request.ReviewRequest;
import com.example.shopflow.dto.response.ReviewResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional

public interface ReviewService {


    ReviewResponse create(String email, ReviewRequest req) ;
    List<ReviewResponse> getByProduct(Long productId) ;


    List<ReviewResponse> getAllPending() ;


     List<ReviewResponse> getAll() ;

    ReviewResponse approve(Long id) ;

    void delete(Long id) ;
}