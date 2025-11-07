package com.billiard_cue_ecommerce_system_be.repository;

import com.billiard_cue_ecommerce_system_be.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    List<Category> findByIsActiveTrue();
    
    boolean existsByName(String name);
}