package com.billiard_cue_ecommerce_system_be.service;

import com.billiard_cue_ecommerce_system_be.dto.request.CreateProductRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.ProductFilterRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.ProductImageRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.UpdateProductRequest;
import com.billiard_cue_ecommerce_system_be.dto.response.PageResponse;
import com.billiard_cue_ecommerce_system_be.dto.response.ProductImageResponse;
import com.billiard_cue_ecommerce_system_be.dto.response.ProductResponse;
import com.billiard_cue_ecommerce_system_be.entity.Category;
import com.billiard_cue_ecommerce_system_be.entity.Product;
import com.billiard_cue_ecommerce_system_be.entity.ProductImage;
import com.billiard_cue_ecommerce_system_be.repository.CategoryRepository;
import com.billiard_cue_ecommerce_system_be.repository.ProductRepository;
import com.billiard_cue_ecommerce_system_be.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductImageRepository productImageRepository;
    
    public PageResponse<ProductResponse> getAllProducts(ProductFilterRequest filter, int page, int size) {
        // Create sort object
        Sort sort = Sort.by(
            "desc".equalsIgnoreCase(filter.getSortDir()) ? Sort.Direction.DESC : Sort.Direction.ASC,
            filter.getSortBy()
        );
        
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Product> productPage = productRepository.findProductsWithFilters(
            filter.getSearch(),
            filter.getCategoryId(),
            filter.getIsActive(),
            pageable
        );
        
        List<ProductResponse> productResponses = productPage.getContent().stream()
                .map(this::mapToProductResponse)
                .toList();
        
        return new PageResponse<>(
                productResponses,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages()
        );
    }
    
    public Optional<ProductResponse> getProductById(Long id) {
        return productRepository.findByIdWithCategoryAndImages(id)
                .map(this::mapToProductResponse);
    }
    
    public ProductResponse createProduct(CreateProductRequest request) {
        // Check if category exists
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
        
        // Check if SKU already exists
        if (request.getSku() != null && productRepository.findBySku(request.getSku()).isPresent()) {
            throw new RuntimeException("SKU đã tồn tại");
        }
        
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setSku(request.getSku());
        product.setImageUrl(request.getImageUrl()); // Backward compatibility
        product.setIsActive(request.getIsActive());
        product.setCategory(category);
        
        Product savedProduct = productRepository.save(product);
        
        // Save images
        saveProductImages(savedProduct, request.getImages());
        
        // Return with images
        return productRepository.findByIdWithCategoryAndImages(savedProduct.getId())
                .map(this::mapToProductResponse)
                .orElseThrow(() -> new RuntimeException("Lỗi khi tải sản phẩm"));
    }
    
    public ProductResponse updateProduct(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        
        // Check if category exists
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
            product.setCategory(category);
        }
        
        // Check if SKU already exists (exclude current product)
        if (request.getSku() != null) {
            Optional<Product> existingProduct = productRepository.findBySku(request.getSku());
            if (existingProduct.isPresent() && !existingProduct.get().getId().equals(id)) {
                throw new RuntimeException("SKU đã tồn tại");
            }
            product.setSku(request.getSku());
        }
        
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getOriginalPrice() != null) product.setOriginalPrice(request.getOriginalPrice());
        if (request.getStockQuantity() != null) product.setStockQuantity(request.getStockQuantity());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl()); // Backward compatibility
        if (request.getIsActive() != null) product.setIsActive(request.getIsActive());
        
        Product savedProduct = productRepository.save(product);
        
        // Update images if provided
        if (request.getImages() != null && !request.getImages().isEmpty()) {
            saveProductImages(savedProduct, request.getImages());
        }
        
        // Return with images
        return productRepository.findByIdWithCategoryAndImages(savedProduct.getId())
                .map(this::mapToProductResponse)
                .orElseThrow(() -> new RuntimeException("Lỗi khi tải sản phẩm"));
    }
    
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        productRepository.delete(product);
    }
    
    public Product toggleProductStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        
        product.setIsActive(!product.getIsActive());
        return productRepository.save(product);
    }
    
    public List<Category> getAllCategories() {
        return categoryRepository.findByIsActiveTrue();
    }
    
    private ProductResponse mapToProductResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setDescription(product.getDescription());
        response.setPrice(product.getPrice());
        response.setOriginalPrice(product.getOriginalPrice());
        response.setStockQuantity(product.getStockQuantity());
        response.setSku(product.getSku());
        response.setImageUrl(product.getImageUrl());
        response.setIsActive(product.getIsActive());
        response.setCategoryId(product.getCategory() != null ? product.getCategory().getId() : null);
        response.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
        response.setCreatedAt(product.getCreatedAt());
        response.setUpdatedAt(product.getUpdatedAt());
        
        // Map images
        if (product.getImages() != null) {
            List<ProductImageResponse> imageResponses = product.getImages().stream()
                    .map(this::mapToProductImageResponse)
                    .collect(Collectors.toList());
            response.setImages(imageResponses);
            
            // Set primary image as imageUrl for backward compatibility
            product.getImages().stream()
                    .filter(img -> img.getIsPrimary())
                    .findFirst()
                    .ifPresent(img -> response.setImageUrl(img.getImageUrl()));
        }
        
        return response;
    }
    
    private ProductImageResponse mapToProductImageResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .isPrimary(image.getIsPrimary())
                .displayOrder(image.getDisplayOrder())
                .build();
    }
    
    private void saveProductImages(Product product, List<ProductImageRequest> imageRequests) {
        if (imageRequests == null || imageRequests.isEmpty()) {
            return;
        }
        
        // Clear existing images
        productImageRepository.deleteByProductId(product.getId());
        
        // Ensure only one primary image
        boolean hasPrimary = imageRequests.stream().anyMatch(img -> img.getIsPrimary());
        if (!hasPrimary && !imageRequests.isEmpty()) {
            imageRequests.get(0).setIsPrimary(true);
        }
        
        // Create new images
        for (int i = 0; i < imageRequests.size(); i++) {
            ProductImageRequest imgReq = imageRequests.get(i);
            ProductImage image = new ProductImage();
            image.setImageUrl(imgReq.getImageUrl());
            image.setIsPrimary(imgReq.getIsPrimary());
            image.setDisplayOrder(imgReq.getDisplayOrder() != null ? imgReq.getDisplayOrder() : i);
            image.setProduct(product);
            
            productImageRepository.save(image);
        }
    }
}