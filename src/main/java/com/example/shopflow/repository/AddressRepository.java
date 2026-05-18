package com.example.shopflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopflow.entity.Address;
import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserId(Long userId);
    java.util.Optional<Address> findByUserIdAndPrincipalTrue(Long userId);
}