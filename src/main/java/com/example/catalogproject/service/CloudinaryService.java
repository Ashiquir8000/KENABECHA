package com.example.catalogproject.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return null;
        try {
            log.info("Uploading image to Cloudinary: {}", file.getOriginalFilename());
            @SuppressWarnings("rawtypes")
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            if (uploadResult != null && uploadResult.get("secure_url") != null) {
                String secureUrl = uploadResult.get("secure_url").toString();
                log.info("Cloudinary upload success. URL: {}", secureUrl);
                return secureUrl;
            }
            log.warn("Cloudinary upload response did not contain secure_url");
            return null;
        } catch (Exception e) {
            log.error("Cloudinary upload failed: {}", e.getMessage(), e);
            return null;
        }
    }
}
