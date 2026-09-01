package com.example.catalogproject.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalFileStorageService implements FileStorageService {
    @Override
    public String storeFile(MultipartFile file) {
        if (file.isEmpty()) return "/images/default.png";
        try {
            String fileName = file.getOriginalFilename();
            Path uploadPath = Paths.get("uploads").toAbsolutePath();
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(fileName);
            file.transferTo(filePath.toFile());
            return "/images/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }
}