package com.example.catalogproject.repository;

import com.example.catalogproject.entity.CustomerOrder;
import com.example.catalogproject.entity.Product;
import com.example.catalogproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {
    List<CustomerOrder> findByUserOrderByOrderDateDesc(User user);
    
}