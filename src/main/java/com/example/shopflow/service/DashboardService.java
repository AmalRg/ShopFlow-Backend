package com.example.shopflow.service;

import com.example.shopflow.dto.response.AdminStatsResponse;
import com.example.shopflow.dto.response.SellerStatsResponse;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)

public interface DashboardService {


     AdminStatsResponse getAdminStats() ;

     SellerStatsResponse getSellerStats(String email);
}