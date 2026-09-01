
package com.example.catalogproject.service;

import com.example.catalogproject.entity.Product;
import com.example.catalogproject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final ProductRepository productRepository;

    public void reduceStock(Product product, int quantity) {
        if (product.getStock() >= quantity) {
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
        } else {
            throw new RuntimeException("Insufficient stock for product: " + product.getName());
        }
    }
}