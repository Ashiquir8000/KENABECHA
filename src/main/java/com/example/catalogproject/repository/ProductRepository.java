package com.example.catalogproject.repository;

import com.example.catalogproject.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProductRepository extends JpaRepository<Product, Integer> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"imageEmbedding"})
    @org.springframework.data.jpa.repository.Query("SELECT p FROM Product p WHERE p.deleted = false")
    java.util.List<Product> findAllActiveWithEmbeddings();
}
