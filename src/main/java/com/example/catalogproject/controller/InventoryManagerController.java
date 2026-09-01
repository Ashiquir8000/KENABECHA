package com.example.catalogproject.controller;

import com.example.catalogproject.service.CatalogueService;
import com.example.catalogproject.service.InventoryManagerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryManagerController {

    private final InventoryManagerService inventoryManagerService;
    private final CatalogueService catalogueService; // ডিপেন্ডেন্সি যুক্ত করা হলো

    @GetMapping
    public String showInventoryDashboard(Model model) {
        model.addAttribute("pendingRequests", inventoryManagerService.getPendingRestockRequests());

        // "admin" ও "moderator" দুটোকেই লিস্টে পাঠানো হচ্ছে
        model.addAttribute("moderators", java.util.List.of(
                catalogueService.getUserByUserName("moderator"),
                catalogueService.getUserByUserName("admin")
        ).stream().filter(java.util.Objects::nonNull).toList());

        return "inventory-dashboard";
    }

    @PostMapping("/restock/fulfill")
    public String fulfillRestock(@RequestParam("requestId") Integer requestId,
                                 @RequestParam("addedQuantity") Integer addedQuantity) {
        inventoryManagerService.fulfillRestockRequest(requestId, addedQuantity);
        return "redirect:/inventory";
    }
}