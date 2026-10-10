package com.example.catalogproject.service;

import com.example.catalogproject.entity.Review;
import com.example.catalogproject.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public void saveReview(Review review) {
        reviewRepository.save(review);
    }

    public void deleteReviewById(Integer id) {
        reviewRepository.deleteById(id);
    }

    public java.util.List<Review> getReviewsByUser(com.example.catalogproject.entity.User user) {
        return reviewRepository.findByUserOrderByIdDesc(user);
    }
}