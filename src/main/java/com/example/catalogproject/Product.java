package com.example.catalogproject;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

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

    @NotBlank(message = "Image URL or file path is required")
    private String imageUrl;

    @NotBlank(message = "Product details are required")
    private String details;
}