package com.example.catalogproject.controller;

import com.example.catalogproject.entity.*;
import com.example.catalogproject.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
public class CatalogController {

    private final CatalogueService catalogueService;
    private final ReviewService reviewService;
    private final CartService cartService;
    private final OrderService orderService;
    private final FileStorageService fileStorageService;
    private final CloudVisionService cloudVisionService;
    private final InventoryManagerService inventoryManagerService;

    @GetMapping("/home")
    public String showHomeInterface() {
        return "home";
    }

    // ==========================================
    // CUSTOMER SITE PROTECTION (Hide Soft-Deleted)
    // ==========================================

    @GetMapping("/category/{catName}")
    public String showCatagoryScene(@PathVariable("catName") String catName, Model model) {
        List<Product> activeCatProducts = catalogueService.findByCategory(catName).stream()
                .filter(p -> !p.isDeleted())
                .toList();
        model.addAttribute("catalu", activeCatProducts);
        model.addAttribute("pageTitle", catName);
        return "category";
    }

    @GetMapping("/proDetail/{id}")
    public String showProductDetailScene(@PathVariable("id") Integer id, Model model) {
        Product existingProduct = catalogueService.getProductById(id);
        // কাস্টমার যেন ডিলিট করা প্রোডাক্টের লিংকে সরাসরি ঢুকতে না পারে
        if (existingProduct != null && !existingProduct.isDeleted()) {
            model.addAttribute("product", existingProduct);
            return "product-detail";
        }
        return "redirect:/home";
    }

    // ==========================================
    // ADMIN DASHBOARD & SOFT DELETE LOGIC
    // ==========================================

    @GetMapping("/adminF")
    public String showAdminDashboard(Model model) {
        List<Product> allProducts = catalogueService.getProducts();

        List<Product> activeProducts = allProducts.stream()
                .filter(p -> !p.isDeleted())
                .toList();

        long archivedCount = allProducts.stream().filter(Product::isDeleted).count();

        // ইউনিক ক্যাটাগরিগুলো বের করা হচ্ছে
        Set<String> uniqueCategories = activeProducts.stream()
                .map(Product::getCategory)
                .collect(Collectors.toSet());

        Map<Integer, Integer> cartCounts = new HashMap<>();
        for(Product p : activeProducts) {
            cartCounts.put(p.getId(), cartService.countCartsWithProduct(p));
        }

        model.addAttribute("catalogue", activeProducts);
        model.addAttribute("cartCounts", cartCounts);
        model.addAttribute("archivedCount", archivedCount);
        model.addAttribute("categories", uniqueCategories);
        return "admin-dashboard";
    }

    @GetMapping("/delete/{id}")
    public String softDeleteProduct(@PathVariable("id") Integer id) {
        Product p = catalogueService.getProductById(id);
        if (p != null) {
            p.setDeleted(true);
            catalogueService.saveProduct(p);
        }
        return "redirect:/adminF";
    }

    @GetMapping("/admin/archived")
    public String showTrashBin(Model model) {
        List<Product> archivedProducts = catalogueService.getProducts().stream()
                .filter(Product::isDeleted)
                .toList();

        Map<Integer, Integer> cartCounts = new HashMap<>();
        for(Product p : archivedProducts) {
            cartCounts.put(p.getId(), cartService.countCartsWithProduct(p));
        }

        model.addAttribute("archivedProducts", archivedProducts);
        model.addAttribute("cartCounts", cartCounts);
        return "admin-archive";
    }

    @GetMapping("/restore/{id}")
    public String restoreProduct(@PathVariable("id") Integer id) {
        Product p = catalogueService.getProductById(id);
        if (p != null) {
            p.setDeleted(false);
            catalogueService.saveProduct(p);
        }
        return "redirect:/admin/archived";
    }

    // ==========================================
    // PRODUCT MANAGEMENT (CREATE & EDIT)
    // ==========================================

    @GetMapping("/prodF")
    public String showProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-form";
    }

    @PostMapping("/prodF")
    public String addProduct(@Valid @ModelAttribute Product product, BindingResult bindingResult,
                             @RequestParam("imageFile") MultipartFile file) {
        if (bindingResult.hasErrors()) {
            return "product-form";
        }

        String imageUrl = fileStorageService.storeFile(file);
        product.setImageUrl(imageUrl);

        if (!file.isEmpty()) {
            try {
                product.setImageEmbedding(cloudVisionService.getEmbeddingFromCloud(file));
            } catch (Exception e) {
                log.error("Failed to generate image embedding from cloud API", e);
            }
        }

        catalogueService.saveProduct(product);
        return "redirect:/adminF";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Integer id, Model model) {
        Product existingProduct = catalogueService.getProductById(id);
        if (existingProduct != null) {
            model.addAttribute("product", existingProduct);
            return "product-form";
        }
        return "redirect:/adminF";
    }

    // ==========================================
    // CART & CHECKOUT
    // ==========================================

    @GetMapping("/buy/{id}")
    public String processBuyNow(@PathVariable("id") Integer productId, Principal principal) {
        User currentUser = catalogueService.getUserByUserName(principal.getName());
        Product product = catalogueService.getProductById(productId);
        if (product != null) {
            cartService.addProductToCart(currentUser, product);
        }
        return "redirect:/checkout";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("productId") Integer productId, Principal principal) {
        User currentUser = catalogueService.getUserByUserName(principal.getName());
        Product product = catalogueService.getProductById(productId);
        if (product != null) {
            cartService.addProductToCart(currentUser, product);
        }
        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Principal principal, Model model) {
        User currentUser = catalogueService.getUserByUserName(principal.getName());
        List<CartItem> cartItems = cartService.getCartItemsByUser(currentUser);
        Double totalAmount = cartService.calculateTotal(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        return "cart";
    }

    @GetMapping("/checkout")
    public String checkoutPage(Principal principal, Model model) {
        User currentUser = catalogueService.getUserByUserName(principal.getName());
        List<CartItem> cartItems = cartService.getCartItemsByUser(currentUser);
        if (cartItems.isEmpty()) {
            return "redirect:/home";
        }
        Double totalAmount = cartService.calculateTotal(cartItems);

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("totalAmount", totalAmount);
        model.addAttribute("user", currentUser);
        return "checkout";
    }

    @PostMapping("/place-order")
    public String placeOrder(@RequestParam("shippingAddress") String shippingAddress, Principal principal) {
        User currentUser = catalogueService.getUserByUserName(principal.getName());
        List<CartItem> cartItems = cartService.getCartItemsByUser(currentUser);
        if (!cartItems.isEmpty()) {
            Double totalAmount = cartService.calculateTotal(cartItems);
            orderService.placeOrder(currentUser, shippingAddress, cartItems, totalAmount);
        }
        return "redirect:/order-success";
    }

    @GetMapping("/order-success")
    public String orderSuccessScene() {
        return "order-success";
    }

    // ==========================================
    // USER AUTHENTICATION & REVIEWS
    // ==========================================

    @GetMapping("/login")
    public String showLogingScene(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid Username or Password!");
        }
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterScene(Model model) {
        User user = new User();
        user.setAddress(new Adress());
        user.setUserProfile(new UserProfile());
        model.addAttribute("user", user);
        return "register";
    }

    @PostMapping("/register")
    public String registerButton(@ModelAttribute User user) {
        catalogueService.saveUserInfo(user);
        return "redirect:/login";
    }

    @PostMapping("/addReview")
    public String addReview(@RequestParam("productId") Integer productId,
                            @RequestParam("comment") String comment,
                            @RequestParam("rating") Integer rating,
                            Principal principal) {

        User currentUser = catalogueService.getUserByUserName(principal.getName());
        Product product = catalogueService.getProductById(productId);

        if (product != null) {
            Review review = new Review();
            review.setComment(comment);
            review.setRating(rating);
            review.setProduct(product);
            review.setUser(currentUser);
            reviewService.saveReview(review);
        }
        return "redirect:/proDetail/" + productId;
    }

    @PostMapping("/deleteReview/{id}")
    public String deleteReview(@PathVariable("id") Integer reviewId,
                               @RequestParam("productId") Integer productId) {
        reviewService.deleteReviewById(reviewId);
        return "redirect:/proDetail/" + productId;
    }

    @PostMapping("/search/visual")
    public String visualSearch(@RequestParam("imageFile") MultipartFile file, Model model) {
        if (file.isEmpty()) return "redirect:/home";

        List<Double> queryVector = cloudVisionService.getEmbeddingFromCloud(file);
        List<Product> allProducts = catalogueService.getProducts();

        List<Product> matchedProducts = allProducts.stream()
                // Visual Search থেকেও ডিলিট হওয়া প্রোডাক্ট বাদ দেওয়া হলো
                .filter(p -> !p.isDeleted() && p.getImageEmbedding() != null && !p.getImageEmbedding().isEmpty())
                .map(p -> new Object() {
                    final Product product = p;
                    final double score = cloudVisionService.calculateCosineSimilarity(queryVector, p.getImageEmbedding());
                })
                .filter(item -> item.score >= 0.65)
                .sorted((a, b) -> Double.compare(b.score, a.score))
                .limit(8)
                .map(item -> item.product)
                .toList();

        model.addAttribute("catalu", matchedProducts);
        model.addAttribute("pageTitle", "Visual Search Results");
        model.addAttribute("hasNoResults", matchedProducts.isEmpty());

        return "category";
    }
}