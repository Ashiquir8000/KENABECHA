package com.example.catalogproject.service;

import com.example.catalogproject.entity.CartItem;
import com.example.catalogproject.entity.Product;
import com.example.catalogproject.entity.User;
import com.example.catalogproject.repository.CartItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartItemRepository cartItemRepository;

    public void addProductToCart(User user, Product product) {
        Optional<CartItem> existingItem = cartItemRepository.findByUserAndProduct(user, product);
        if (existingItem.isPresent()) {
            CartItem cartItem = existingItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItemRepository.save(cartItem);
        } else {
            CartItem newItem = new CartItem();
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setQuantity(1);
            cartItemRepository.save(newItem);
        }
    }

    public List<CartItem> getCartItemsByUser(User user) {
        return cartItemRepository.findByUser(user);
    }

    public Double calculateTotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }

    public void clearCart(User user) {
        cartItemRepository.deleteByUser(user);
    }


    public int countCartsWithProduct(Product product) {
        return cartItemRepository.countByProduct(product);
    }

    @Transactional
    public void removeProductFromAllCarts(Product product) {
        cartItemRepository.deleteByProduct(product);
    }
}