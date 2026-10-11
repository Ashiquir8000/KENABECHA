package com.example.catalogproject.controller;

import com.example.catalogproject.entity.CustomerOrder;
import com.example.catalogproject.entity.Review;
import com.example.catalogproject.entity.User;
import com.example.catalogproject.service.CatalogueService;
import com.example.catalogproject.service.OrderService;
import com.example.catalogproject.service.ReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ProfileController {

    private final CatalogueService catalogueService;
    private final OrderService orderService;
    private final ReviewService reviewService;

    @GetMapping("/profile")
    public String viewProfile(Principal principal,
                              @AuthenticationPrincipal UserDetails userDetails,
                              HttpSession session,
                              Model model) {
        String username = resolveUsername(principal, userDetails, session);
        if (username == null) {
            log.warn("Unauthenticated attempt to access /profile");
            return "redirect:/login";
        }

        try {
            User user = catalogueService.getUserByUserName(username);
            if (user == null) {
                log.warn("User '{}' not found in database for profile view", username);
                return "redirect:/login";
            }

            List<CustomerOrder> orders = orderService.getOrdersByUser(user);
            List<Review> reviews = reviewService.getReviewsByUser(user);

            model.addAttribute("user", user);
            model.addAttribute("orders", orders != null ? orders : Collections.emptyList());
            model.addAttribute("reviews", reviews != null ? reviews : Collections.emptyList());

            return "profile";
        } catch (Exception e) {
            log.error("Error loading profile for user '{}'", username, e);
            model.addAttribute("errorMessage", "Unable to load complete profile data.");
            return "profile";
        }
    }

    @PostMapping("/profile/update")
    public String updateProfile(Principal principal,
                                @AuthenticationPrincipal UserDetails userDetails,
                                HttpSession session,
                                @RequestParam(value = "mobileNumbers", required = false) String mobileNumbers,
                                @RequestParam(value = "userPassword", required = false) String userPassword,
                                @RequestParam(value = "userEmail", required = false) String userEmail,
                                RedirectAttributes redirectAttributes) {
        String username = resolveUsername(principal, userDetails, session);
        if (username == null) {
            log.warn("Unauthenticated attempt to post to /profile/update");
            return "redirect:/login";
        }

        try {
            User existingUser = catalogueService.getUserByUserName(username);
            if (existingUser == null) {
                log.warn("User '{}' not found for profile update", username);
                return "redirect:/login";
            }

            // Update mobile numbers safely
            if (mobileNumbers != null) {
                List<String> numbersList = Arrays.stream(mobileNumbers.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toCollection(ArrayList::new));
                existingUser.setMobileNumbers(numbersList);
            }

            // Update email safely if provided
            if (userEmail != null && !userEmail.trim().isEmpty()) {
                existingUser.setUserEmail(userEmail.trim());
            }

            // Only update password if a new non-empty password was provided
            if (userPassword != null && !userPassword.trim().isEmpty()) {
                existingUser.setUserPassword(userPassword.trim());
            }

            catalogueService.saveUserInfo(existingUser);
            if (session != null) {
                session.setAttribute("loggedInUser", existingUser);
            }

            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
            log.info("Profile successfully updated for user '{}'", username);
        } catch (Exception e) {
            log.error("Failed to update profile for user '{}'", username, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to update profile: " + e.getMessage());
        }

        return "redirect:/profile?success";
    }

    private String resolveUsername(Principal principal, UserDetails userDetails, HttpSession session) {
        if (principal != null && principal.getName() != null && !principal.getName().isEmpty()) {
            return principal.getName();
        }
        if (userDetails != null && userDetails.getUsername() != null && !userDetails.getUsername().isEmpty()) {
            return userDetails.getUsername();
        }
        if (session != null && session.getAttribute("loggedInUser") instanceof User user) {
            return user.getUserName();
        }
        return null;
    }
}
