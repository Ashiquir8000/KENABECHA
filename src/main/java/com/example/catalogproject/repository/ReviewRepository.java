package com.example.catalogproject.repository;

import com.example.catalogproject.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.catalogproject.entity.User;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByUserOrderByIdDesc(User user);
}