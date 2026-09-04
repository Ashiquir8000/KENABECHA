package com.example.catalogproject.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class CloudVisionService {

    public List<Double> getEmbeddingFromCloud(MultipartFile file) {
        if (file == null || file.isEmpty()) return new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            return extractFeatureVector(is);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }


    public List<Double> getEmbeddingFromInputStream(InputStream inputStream) {
        if (inputStream == null) return new ArrayList<>();
        return extractFeatureVector(inputStream);
    }


    private List<Double> extractFeatureVector(InputStream is) {
        try {
            BufferedImage original = ImageIO.read(is);
            if (original == null) return new ArrayList<>();

            int targetSize = 16;
            BufferedImage resized = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = resized.createGraphics();
            g.drawImage(original, 0, 0, targetSize, targetSize, null);
            g.dispose();

            List<Double> vector = new ArrayList<>();
            double sumSq = 0.0;

            for (int y = 0; y < targetSize; y++) {
                for (int x = 0; x < targetSize; x++) {
                    int rgb = resized.getRGB(x, y);
                    double r = ((rgb >> 16) & 0xFF) / 255.0;
                    double gVal = ((rgb >> 8) & 0xFF) / 255.0;
                    double b = (rgb & 0xFF) / 255.0;

                    vector.add(r);
                    vector.add(gVal);
                    vector.add(b);

                    sumSq += (r * r) + (gVal * gVal) + (b * b);
                }
            }


            double magnitude = Math.sqrt(sumSq);
            if (magnitude > 0) {
                for (int i = 0; i < vector.size(); i++) {
                    vector.set(i, vector.get(i) / magnitude);
                }
            }
            return vector;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public double calculateCosineSimilarity(List<Double> vecA, List<Double> vecB) {
        if (vecA == null || vecB == null || vecA.size() != vecB.size() || vecA.isEmpty()) {
            return 0.0;
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vecA.size(); i++) {
            dotProduct += vecA.get(i) * vecB.get(i);
            normA += Math.pow(vecA.get(i), 2);
            normB += Math.pow(vecB.get(i), 2);
        }

        if (normA == 0 || normB == 0) return 0.0;
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}