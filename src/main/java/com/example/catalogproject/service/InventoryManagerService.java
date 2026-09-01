package com.example.catalogproject.service;

import com.example.catalogproject.entity.Product;
import com.example.catalogproject.entity.RestockRequest;
import com.example.catalogproject.entity.User;
import com.example.catalogproject.repository.ProductRepository;
import com.example.catalogproject.repository.RestockRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryManagerService {

    private final RestockRequestRepository restockRequestRepository;
    private final ProductRepository productRepository;

    public List<RestockRequest> getPendingRestockRequests() {
        return restockRequestRepository.findByStatusOrderByRequestTimeDesc("PENDING");
    }

    @Transactional
    public void createRestockRequest(Product product, User moderator, Integer quantity) {
        RestockRequest req = new RestockRequest();
        req.setProduct(product);
        req.setRequestedBy(moderator);
        req.setRequestedQuantity(quantity);
        req.setStatus("PENDING");
        req.setRequestTime(LocalDateTime.now());
        restockRequestRepository.save(req);
    }

    @Transactional
    public void fulfillRestockRequest(Integer requestId, Integer addedQuantity) {
        RestockRequest request = restockRequestRepository.findById(requestId).orElse(null);
        if (request != null && "PENDING".equals(request.getStatus())) {
            Product product = request.getProduct();
            product.setStock(product.getStock() + addedQuantity);
            productRepository.save(product);

            request.setStatus("FULFILLED");
            request.setFulfilledTime(LocalDateTime.now());
            restockRequestRepository.save(request);
        }
    }


    @Transactional
    public void removeRestockRequestsForProduct(Product product) {
        restockRequestRepository.deleteByProduct(product);
    }
}