package com.example.shopflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopflow.entity.User;
import com.example.shopflow.enums.Role;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
}