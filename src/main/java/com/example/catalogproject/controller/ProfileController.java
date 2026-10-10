package com.example.catalogproject.controller;

import com.example.catalogproject.entity.CustomerOrder;
import com.example.catalogproject.entity.Review;
import com.example.catalogproject.entity.User;
import com.example.catalogproject.service.CatalogueService;
import com.example.catalogproject.service.OrderService;
import com.example.catalogproject.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final CatalogueService catalogueService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        
        User user = catalogueService.getUserByUserName(userDetails.getUsername());
        if (user == null) {
            return "redirect:/login";
        }

        List<CustomerOrder> orders = orderService.getOrdersByUser(user);
        List<Review> reviews = reviewService.getReviewsByUser(user);

        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        model.addAttribute("reviews", reviews);

        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                @ModelAttribute("user") User updatedUser,
                                RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            return "redirect:/login";
        }

        User existingUser = catalogueService.getUserByUserName(userDetails.getUsername());
        if (existingUser != null) {
            existingUser.setMobileNumbers(updatedUser.getMobileNumbers());
            // Only update password if a new one was provided, otherwise keep existing
            if (updatedUser.getUserPassword() != null && !updatedUser.getUserPassword().isEmpty()) {
                existingUser.setUserPassword(updatedUser.getUserPassword());
            }
            catalogueService.saveUserInfo(existingUser);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        }

        return "redirect:/profile";
    }
}

