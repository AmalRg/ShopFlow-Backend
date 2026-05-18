
package com.example.shopflow.serviceImpl;

import com.example.shopflow.dto.request.CategoryRequest;
import com.example.shopflow.dto.response.CategoryResponse;
import com.example.shopflow.entity.Category;
import com.example.shopflow.exception.BusinessException;
import com.example.shopflow.exception.ResourceNotFoundException;
import com.example.shopflow.mapper.CategoryMapper;
import com.example.shopflow.repository.CategoryRepository;
import com.example.shopflow.repository.ProductRepository;
import com.example.shopflow.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository  productRepository;
    private final CategoryMapper categoryMapper;

    @Transactional(readOnly = true)
    @Override
    public List<CategoryResponse> getAll() {
        return categoryRepository.findByParentIsNull()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }
    @Override
    public CategoryResponse create(CategoryRequest req) {
        Category category = categoryMapper.toEntity(req);

        if (categoryRepository.existsByNom(category.getNom()))
            throw new BusinessException(
                    "Catégorie déjà existante : " + category.getNom());

        if (hasParent(category)) {
            Category parent = findParentById(category.getParent().getId());
            category.setParent(parent);
        }

        return categoryMapper.toDto(categoryRepository.save(category));
    }
    @Override
    public CategoryResponse update(Long id, CategoryRequest req) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catégorie non trouvée : " + id));

        category.setNom(req.getNom());
        category.setDescription(req.getDescription());

        if (req.getParentId() != null) {
            if (req.getParentId().equals(id))
                throw new BusinessException(
                        "Une catégorie ne peut pas être son propre parent");
            Category parent = findParentById(req.getParentId());
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        return categoryMapper.toDto(categoryRepository.save(category));
    }
    @Override
    public void delete(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catégorie non trouvée : " + id));

        if (category.getSousCategories() != null)
            category.getSousCategories().forEach(sub -> sub.setParent(null));

        categoryRepository.delete(category);
    }

    // ── Helpers ───────────────────────────────────────────────

    private boolean hasParent(Category category) {
        return category.getParent() != null
                && category.getParent().getId() != null;
    }

    private Category findParentById(Long parentId) {
        return categoryRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catégorie parente non trouvée : " + parentId));
    }
}


