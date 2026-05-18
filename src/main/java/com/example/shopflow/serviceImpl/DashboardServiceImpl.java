package com.example.shopflow.serviceImpl;

import com.example.shopflow.dto.response.AdminStatsResponse;
import com.example.shopflow.dto.response.SellerStatsResponse;
import com.example.shopflow.exception.ResourceNotFoundException;
import com.example.shopflow.mapper.DashboardMapper;
import com.example.shopflow.repository.OrderRepository;
import com.example.shopflow.repository.ProductRepository;
import com.example.shopflow.repository.UserRepository;
import com.example.shopflow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final OrderRepository   orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository    userRepository;
    private final DashboardMapper   dashboardMapper;
    @Override
    public AdminStatsResponse getAdminStats()  {
        return dashboardMapper.toAdminStats(
                orderRepository.totalRevenue(),
                orderRepository.count(),
                productRepository.count(),
                userRepository.count(),
                productRepository.findTopSelling(PageRequest.of(0, 5))
        );
    }
    @Override
    public SellerStatsResponse getSellerStats(String email) {
        var seller = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vendeur non trouvé"));

        return dashboardMapper.toSellerStats(
                orderRepository.totalRevenueBySeller(seller.getId()),
                orderRepository.countPendingBySeller(seller.getId()),
                productRepository.findBySellerId(seller.getId()).size()
        );
    }
}