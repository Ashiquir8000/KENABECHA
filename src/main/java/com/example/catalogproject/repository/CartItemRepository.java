package com.example.catalogproject.repository;

import com.example.catalogproject.entity.CartItem;
import com.example.catalogproject.entity.Product;
import com.example.catalogproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    List<CartItem> findByUser(User user);
    Optional<CartItem> findByUserAndProduct(User user, Product product);

    
    int countByProduct(Product product);

    @Transactional
    void deleteByUser(User user);


    // আগের কোডের ঠিক নিচে এটি যোগ করুন
    @Transactional
    void deleteByProduct(Product product);
}