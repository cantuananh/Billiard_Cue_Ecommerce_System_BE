package com.billiard_cue_ecommerce_system_be.controller.admin;

import com.billiard_cue_ecommerce_system_be.dto.request.CreateProductRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.ProductFilterRequest;
import com.billiard_cue_ecommerce_system_be.dto.request.UpdateProductRequest;
import com.billiard_cue_ecommerce_system_be.dto.response.ImageUploadResponse;
import com.billiard_cue_ecommerce_system_be.dto.response.PageResponse;
import com.billiard_cue_ecommerce_system_be.dto.response.ProductResponse;
import com.billiard_cue_ecommerce_system_be.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminProductController {
    
    private final ProductService productService;

    @GetMapping
    public ResponseEntity<PageResponse<ProductResponse>> getAllProducts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        ProductFilterRequest filter = ProductFilterRequest.builder()
                .search(search)
                .categoryId(categoryId)
                .isActive(isActive)
                .sortBy(sortBy)
                .sortDir(sortDir)
                .build();
        
        PageResponse<ProductResponse> products = productService.getAllProducts(filter, page, size);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        Optional<ProductResponse> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody CreateProductRequest request) {
        try {
            ProductResponse product = productService.createProduct(request);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long id, 
                                                        @Valid @RequestBody UpdateProductRequest request) {
        try {
            ProductResponse product = productService.updateProduct(id, request);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ProductResponse> toggleProductStatus(@PathVariable Long id) {
        try {
            ProductResponse product = productService.toggleProductStatus(id);
            return ResponseEntity.ok(product);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadProductImage(@RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is empty");
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("File must be an image");
            }
            
            // Create filename
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String imageUrl = "/uploads/products/" + fileName;
            
            // Create upload directory if it doesn't exist
            java.io.File uploadDir = new java.io.File(System.getProperty("user.dir"), "uploads/products");
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            
            // Save file to disk
            java.io.File destinationFile = new java.io.File(uploadDir, fileName);
            file.transferTo(destinationFile);
            
            return ResponseEntity.ok(new ImageUploadResponse(imageUrl, fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to upload file: " + e.getMessage());
        }
    }

    // Simple response class for image upload
    public static class ImageUploadResponse {
        private String imageUrl;
        private String fileName;

        public ImageUploadResponse(String imageUrl, String fileName) {
            this.imageUrl = imageUrl;
            this.fileName = fileName;
        }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
    }
}