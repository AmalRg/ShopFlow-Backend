package com.example.shopflow.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {

    // Setters
    // Getters
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Getter
    @Column(nullable = false, unique = true)
    private String nom;

    @Setter
    @Getter
    private String description;

    // @JsonIgnore sur parent pour éviter la récursion
    @Setter
    @Getter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Category parent;

    @Transient
    private Long parentId;

    @Transient
    private String parentNom;

    @Setter
    @Getter
    @OneToMany(mappedBy = "parent",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.EAGER)
    @JsonIgnoreProperties("parent")
    private List<Category> sousCategories = new ArrayList<>();
    public Long getParentId() {
        return parent != null ? parent.getId() : null;
    }
    public String getParentNom() {
        return parent != null ? parent.getNom() : null;
    }
}