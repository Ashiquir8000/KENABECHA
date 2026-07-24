package com.example.catalogproject;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogInterface extends JpaRepository<Product, Integer> {
}
