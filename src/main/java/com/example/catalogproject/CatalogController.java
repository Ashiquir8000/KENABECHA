package com.example.catalogproject;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CatalogController {


    private final CatalogInterface catalogInterface;

    @GetMapping("/prodF")
    public String showProductForm()
    {
    return "product-form";
    }

    @GetMapping("/adminF")
    public String showAdminDashboard(Model model)
    {
        model.addAttribute("catalogue", catalogInterface.findAll());
        return "admin-dashboard";
    }

    @PostMapping("/prodF")
    public String addProduct(@Valid @ModelAttribute Product product, BindingResult bindingResult)
    {
        if(bindingResult.hasErrors())
        {
            return "product-form";
        }

       catalogInterface.save(product);
        return "redirect:/admin-dashboard";

    }
}
