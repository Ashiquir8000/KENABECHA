package com.example.catalogproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@org.springframework.cache.annotation.EnableCaching
public class CatalogProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(CatalogProjectApplication.class, args);
    }

}
