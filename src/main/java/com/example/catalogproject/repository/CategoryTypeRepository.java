package com.example.catalogproject.repository;

import com.example.catalogproject.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryTypeRepository extends JpaRepository<Product, Integer> {

    List<Product> findByCategory(String category);
}
