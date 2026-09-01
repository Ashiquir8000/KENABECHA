package com.example.catalogproject.config;

import com.example.catalogproject.entity.Product;
import com.example.catalogproject.repository.ProductRepository;
import com.example.catalogproject.service.CloudVisionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataVectorInitializer implements CommandLineRunner {

    private final ProductRepository productRepository;
    private final CloudVisionService visionService;

    @Override
    public void run(String... args) {
        List<Product> products = productRepository.findAll();
        for (Product product : products) {
            if (product.getImageEmbedding() == null || product.getImageEmbedding().isEmpty()) {
                if (product.getImageUrl() != null && product.getImageUrl().startsWith("/images/")) {
                    String fileName = product.getImageUrl().replace("/images/", "");
                    Path filePath = Paths.get("uploads").toAbsolutePath().resolve(fileName);
                    File imageFile = filePath.toFile();

                    if (imageFile.exists()) {
                        try (FileInputStream input = new FileInputStream(imageFile)) {
                            List<Double> embedding = visionService.getEmbeddingFromInputStream(input);
                            product.setImageEmbedding(embedding);
                            productRepository.save(product);
                            log.info("Successfully generated embedding for product: {}", product.getName());
                        } catch (Exception e) {
                            log.error("Failed to generate vector for: {}", product.getName(), e);
                        }
                    }
                }
            }
        }
    }
}