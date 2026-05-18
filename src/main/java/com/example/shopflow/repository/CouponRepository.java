package com.example.shopflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopflow.entity.Coupon;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
    boolean existsByCode(String code);
}
