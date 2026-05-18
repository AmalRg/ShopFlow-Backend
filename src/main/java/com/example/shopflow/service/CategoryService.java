
package com.example.shopflow.service;

import com.example.shopflow.dto.request.CategoryRequest;
import com.example.shopflow.dto.response.CategoryResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional

public interface CategoryService {

    List<CategoryResponse> getAll();


   CategoryResponse create(CategoryRequest req);

   CategoryResponse update(Long id, CategoryRequest req);

    void delete(Long id);
}

//package com.example.shopflow.service;
//
//import com.example.shopflow.repository.ProductRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import com.example.shopflow.entity.Category;
//import com.example.shopflow.entity.Product;
//import com.example.shopflow.exception.BusinessException;
//import com.example.shopflow.exception.ResourceNotFoundException;
//import com.example.shopflow.repository.CategoryRepository;
//
//import java.util.List;
//
//@Service
//@Transactional
//public class CategoryService {
//
//    private final CategoryRepository categoryRepository;
//    private final ProductRepository productRepository;
//
//    public CategoryService(CategoryRepository categoryRepository,
//                           ProductRepository productRepository) {
//        this.categoryRepository = categoryRepository;
//        this.productRepository  = productRepository;
//    }
//
//    @Transactional(readOnly = true)
//    public List<Category> getAll() {
//        return categoryRepository.findByParentIsNull();
//    }
//
//    public Category create(Category category) {
//        if (categoryRepository.existsByNom(category.getNom()))
//            throw new BusinessException(
//                    "Catégorie déjà existante : " + category.getNom());
//
//        if (category.getParent() != null
//                && category.getParent().getId() != null) {
//            Category parent = categoryRepository
//                    .findById(category.getParent().getId())
//                    .orElseThrow(() -> new ResourceNotFoundException(
//                            "Catégorie parente non trouvée"));
//            category.setParent(parent);
//        }
//        return categoryRepository.save(category);
//    }
//
//    public Category update(Long id, Category req) {
//        Category category = categoryRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException(
//                        "Catégorie non trouvée : " + id));
//
//        category.setNom(req.getNom());
//        category.setDescription(req.getDescription());
//
//        if (req.getParent() != null && req.getParent().getId() != null) {
//            if (req.getParent().getId().equals(id))
//                throw new BusinessException(
//                        "Une catégorie ne peut pas être son propre parent");
//            Category parent = categoryRepository
//                    .findById(req.getParent().getId())
//                    .orElseThrow(() -> new ResourceNotFoundException(
//                            "Catégorie parente non trouvée"));
//            category.setParent(parent);
//        } else {
//            category.setParent(null);
//        }
//        return categoryRepository.save(category);
//    }
//
//    @Transactional
//    public void delete(Long id) {
//        Category category = categoryRepository.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException("Catégorie non trouvée"));
//        if (category.getSousCategories() != null) {
//            category.getSousCategories().forEach(sub -> sub.setParent(null));
//        }
//
//        categoryRepository.delete(category);
//    }
//}