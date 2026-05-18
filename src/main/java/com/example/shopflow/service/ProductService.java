package com.example.shopflow.service;

import com.example.shopflow.dto.request.ProductRequest;
import com.example.shopflow.dto.response.ProductResponse;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional

public interface ProductService {



     Page<ProductResponse> getAll(Long categoryId, BigDecimal minPrix,
                                        BigDecimal maxPrix, Long sellerId,
                                        boolean promoOnly, int page, int size,
                                        String sortBy);


     ProductResponse getById(Long id) ;
     Page<ProductResponse> search(String q, int page, int size) ;


   List<ProductResponse> topSelling() ;

    ProductResponse create(ProductRequest req, String sellerEmail) ;

     ProductResponse update(Long id, ProductRequest req, String email) ;

   void delete(Long id) ;
}