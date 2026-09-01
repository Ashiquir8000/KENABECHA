package com.example.catalogproject.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class RestockRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "requested_by_id")
    private User requestedBy; // Moderator

    private Integer requestedQuantity;
    private String status; // PENDING, FULFILLED
    private LocalDateTime requestTime;
    private LocalDateTime fulfilledTime;
}