package com.example.catalogproject.controller;

import com.example.catalogproject.entity.Product;
import com.example.catalogproject.entity.Review;
import com.example.catalogproject.entity.User;
import com.example.catalogproject.repository.ChatMessageRepository;
import com.example.catalogproject.repository.ProductRepository;
import com.example.catalogproject.repository.ReviewRepository;
import com.example.catalogproject.service.CatalogueService;
import com.example.catalogproject.service.InventoryManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ModeratorController {

    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;
    private final ReviewRepository reviewRepository;

    // নতুন সার্ভিস ইনজেকশন
    private final CatalogueService catalogueService;
    private final InventoryManagerService inventoryManagerService;

    @GetMapping("/moderator")
    public String showModeratorHub(Model model) {

        List<User> chatUsers = chatMessageRepository.findDistinctChatUsers();

        List<Product> lowStockProducts = productRepository.findAll().stream()
                .filter(p -> p.getStock() <= 5)
                .collect(Collectors.toList());

        List<String> abusiveKeywords = List.of(
                "baje", "kharap", "fake", "fraud", "faltu", "chur", "scam", "worst", "terrible", "cheat"
        );

        List<Review> flaggedReviews = reviewRepository.findAll().stream()
                .filter(r -> {
                    boolean isLowRating = r.getRating() != null && r.getRating() <= 2;

                    String commentText = (r.getComment() != null) ? r.getComment().toLowerCase() : "";
                    boolean containsBadWords = abusiveKeywords.stream()
                            .anyMatch(word -> commentText.contains(word.toLowerCase()));

                    return isLowRating || containsBadWords;
                })
                .collect(Collectors.toList());

        model.addAttribute("chatUsers", chatUsers);
        model.addAttribute("lowStockProducts", lowStockProducts);
        model.addAttribute("flaggedReviews", flaggedReviews);

        User inventoryUser = catalogueService.getUserByUserName("inventory");
        model.addAttribute("inventoryUser", inventoryUser);

        return "moderator";
    }


    @PostMapping("/moderator/restock/request")
    public String sendRestockRequest(@RequestParam("productId") Integer productId,
                                     @RequestParam("requestedQuantity") Integer quantity,
                                     Principal principal) {
        Product product = catalogueService.getProductById(productId);
        User moderator = catalogueService.getUserByUserName(principal.getName());

        if (product != null && moderator != null) {
            inventoryManagerService.createRestockRequest(product, moderator, quantity);
        }
        return "redirect:/moderator";
    }
}