package com.example.shopflow.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "seller_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomBoutique;
    private String description;
    private String logo;

    @Builder.Default
    private Double note = 0.0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
}