package com.billiard_cue_ecommerce_system_be.service;

import com.billiard_cue_ecommerce_system_be.dto.request.CategoryFilterRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.CreateCategoryRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.UpdateCategoryRequest;
import com.billiard_cue_ecommerce_system_be.dto.response.CategoryResponse;
import com.billiard_cue_ecommerce_system_be.dto.response.PageResponse;
import com.billiard_cue_ecommerce_system_be.entity.Category;
import com.billiard_cue_ecommerce_system_be.repository.CategoryRepository;
import com.billiard_cue_ecommerce_system_be.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    
    public PageResponse<CategoryResponse> getAllCategories(CategoryFilterRequest filter, int page, int size) {
        // Tạo Specification cho filter
        Specification<Category> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // Search filter
            if (filter.getSearch() != null && !filter.getSearch().trim().isEmpty()) {
                String searchTerm = "%" + filter.getSearch().toLowerCase() + "%";
                predicates.add(criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("name")), searchTerm));
            }
            
            // Status filter
            if (filter.getIsActive() != null) {
                predicates.add(criteriaBuilder.equal(root.get("isActive"), filter.getIsActive()));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        // Tạo Sort với default values
        String sortBy = filter.getSortBy() != null ? filter.getSortBy() : "name";
        String sortDir = filter.getSortDir() != null ? filter.getSortDir() : "asc";
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Query với specification
        Page<Category> categoryPage = categoryRepository.findAll(spec, pageable);
        
        // Convert to CategoryResponse
        List<CategoryResponse> categoryResponses = categoryPage.getContent().stream()
                .map(this::mapToCategoryResponse)
                .toList();
        
        return new PageResponse<>(
                categoryResponses,
                categoryPage.getNumber(),
                categoryPage.getSize(),
                categoryPage.getTotalElements(),
                categoryPage.getTotalPages()
        );
    }
    
    public Optional<CategoryResponse> getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(this::mapToCategoryResponse);
    }
    
    @Transactional
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        // Kiểm tra tên danh mục đã tồn tại
        if (categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }
        
        Category category = new Category();
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive());
        
        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(savedCategory);
    }
    
    @Transactional
    public CategoryResponse updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        
        // Kiểm tra tên danh mục đã tồn tại (trừ chính nó)
        if (!category.getName().equals(request.getName()) && 
            categoryRepository.existsByName(request.getName())) {
            throw new RuntimeException("Tên danh mục đã tồn tại");
        }
        
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setIsActive(request.getIsActive());
        
        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(savedCategory);
    }
    
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        
        // Kiểm tra có sản phẩm nào đang sử dụng danh mục này không
        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new RuntimeException("Không thể xóa danh mục đang có sản phẩm");
        }
        
        categoryRepository.delete(category);
    }
    
    @Transactional
    public CategoryResponse toggleCategoryStatus(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        
        category.setIsActive(!category.getIsActive());
        Category savedCategory = categoryRepository.save(category);
        return mapToCategoryResponse(savedCategory);
    }
    
    public List<CategoryResponse> getActiveCategories() {
        List<Category> categories = categoryRepository.findByIsActiveTrue();
        return categories.stream()
                .map(this::mapToCategoryResponse)
                .toList();
    }
    
    private CategoryResponse mapToCategoryResponse(Category category) {
        Long productCount = productRepository.countByCategoryId(category.getId());
        
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .productCount(productCount)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}