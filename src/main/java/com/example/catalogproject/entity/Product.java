package com.example.catalogproject.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product {

    @Id
    @NotNull(message = "ID is required")
    private Integer id;

    @NotBlank(message = "Product name is required")
    private String name;

    @NotBlank(message = "Please select a category")
    private String category;

    @NotNull(message = "Price is required")
    @DecimalMax(value = "99999.99", message = "Price must be less than 100,000")
    @DecimalMin(value = "0.1", message = "Price must be greater than 0")
    private double price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    @Max(value = 999, message = "Stock must be less than 1000")
    private int stock;

    private String imageUrl;

    @NotBlank(message = "Product details are required")
    private String details;


    @Column(name = "is_deleted")
    private Boolean deleted = false;

    public boolean isDeleted() {
        return this.deleted != null && this.deleted;
    }

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_image_embeddings", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "embedding_value")
    private List<Double> imageEmbedding;
}