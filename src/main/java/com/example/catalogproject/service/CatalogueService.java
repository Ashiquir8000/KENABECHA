package com.example.catalogproject.service;

import com.example.catalogproject.entity.Product;
import com.example.catalogproject.entity.User;
import com.example.catalogproject.repository.CategoryTypeRepository;
import com.example.catalogproject.repository.ProductRepository;
import com.example.catalogproject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CatalogueService {
    private final ProductRepository productRepository;
    private final CategoryTypeRepository categoryTypeRepository;
    private final UserRepository userRepository;

    public List<Product> findByCategory(String category) {
        return categoryTypeRepository.findByCategory(category);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProductByID(Integer id) {
        productRepository.deleteById(id);
    }

    public User getUserByNameAndPassword(String username, String password) {
        return userRepository.findByUserNameAndUserPassword(username, password);
    }

    public User saveUserInfo(User user) {
        return userRepository.save(user);
    }

    public User getUserByUserName(String username) {
        return userRepository.findByUserName(username);
    }


}