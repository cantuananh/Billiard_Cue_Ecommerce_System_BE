package com.billiard_cue_ecommerce_system_be.controller;

import com.billiard_cue_ecommerce_system_be.dto.response.ProductResponse;
import com.billiard_cue_ecommerce_system_be.dto.response.PageResponse;
import com.billiard_cue_ecommerce_system_be.dto.request.ProductFilterRequest;
import com.billiard_cue_ecommerce_system_be.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/public/products")
@CrossOrigin(origins = "http://localhost:5173")
public class PublicProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        ProductFilterRequest filterRequest = new ProductFilterRequest();
        filterRequest.setCategoryId(categoryId);
        filterRequest.setSearch(search);
        filterRequest.setMinPrice(minPrice);
        filterRequest.setMaxPrice(maxPrice);
        filterRequest.setSortBy(sortBy);
        filterRequest.setSortDir(sortDir);
        
        PageResponse<ProductResponse> products = productService.getAllProducts(filterRequest, page, size);
        
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        try {
            return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<List<ProductResponse>> getFeaturedProducts(
            @RequestParam(defaultValue = "8") int limit) {
        
        ProductFilterRequest filterRequest = new ProductFilterRequest();
        filterRequest.setSortBy("createdAt");
        filterRequest.setSortDir("desc");
        
        PageResponse<ProductResponse> featuredProducts = productService.getAllProducts(filterRequest, 0, limit);
        
        return ResponseEntity.ok(featuredProducts.getContent());
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<PageResponse<ProductResponse>> getProductsByCategory(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ProductFilterRequest filterRequest = new ProductFilterRequest();
        filterRequest.setCategoryId(categoryId);
        filterRequest.setSortBy("createdAt");
        filterRequest.setSortDir("desc");
        
        PageResponse<ProductResponse> products = productService.getAllProducts(filterRequest, page, size);
        
        return ResponseEntity.ok(products);
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductResponse>> searchProducts(
            @RequestParam String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        ProductFilterRequest filterRequest = new ProductFilterRequest();
        filterRequest.setSearch(search);
        filterRequest.setSortBy("createdAt");
        filterRequest.setSortDir("desc");
        
        PageResponse<ProductResponse> products = productService.getAllProducts(filterRequest, page, size);
        
        return ResponseEntity.ok(products);
    }
}