package com.example.catalogproject.repository;

import com.example.catalogproject.entity.Product;
import com.example.catalogproject.entity.RestockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RestockRequestRepository extends JpaRepository<RestockRequest, Integer> {
    List<RestockRequest> findByStatusOrderByRequestTimeDesc(String status);

    @Transactional
    void deleteByProduct(Product product);
}