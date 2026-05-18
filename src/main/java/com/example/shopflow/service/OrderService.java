package com.example.shopflow.service;

import com.example.shopflow.dto.request.OrderRequest;
import com.example.shopflow.dto.response.OrderResponse;
import com.example.shopflow.enums.OrderStatus;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional

public interface OrderService {



     OrderResponse createFromCart(String email, OrderRequest req) ;



    List<OrderResponse> getMyOrders(String email) ;


     OrderResponse getById(Long id) ;

     List<OrderResponse> getAll() ;
     OrderResponse updateStatus(Long id, OrderStatus newStatus) ;

     OrderResponse cancel(Long id, String email) ;

    List<OrderResponse> getByCustomer(String email) ;




}