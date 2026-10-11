package com.example.catalogproject.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class LocalFileStorageService implements FileStorageService {

    @Value("${file.upload-dir:uploads/images}")
    private String uploadDir;

    @Override
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "/images/default.png";
        }
        try {
            String originalFilename = file.getOriginalFilename();
            String cleanName = (originalFilename != null && !originalFilename.trim().isEmpty())
                    ? Paths.get(originalFilename).getFileName().toString()
                    : "product_" + System.currentTimeMillis() + ".png";

            Path uploadPath = Paths.get(uploadDir).toAbsolutePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path targetPath = uploadPath.resolve(cleanName);
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return "/images/" + cleanName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}