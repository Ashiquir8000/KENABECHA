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
    private User requestedBy;

    private Integer requestedQuantity;
    private String status;
    private LocalDateTime requestTime;
    private LocalDateTime fulfilledTime;
}