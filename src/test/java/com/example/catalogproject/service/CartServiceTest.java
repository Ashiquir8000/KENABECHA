package com.example.catalogproject.service;

import com.example.catalogproject.entity.CartItem;
import com.example.catalogproject.entity.Product;
import com.example.catalogproject.entity.User;
import com.example.catalogproject.repository.CartItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class CartServiceTest {

    @MockitoBean
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartService cartService;

    private User user;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);

        product = new Product();
        product.setId(101);
        product.setPrice(1500.0);
    }

    @Test
    void testAddProductToCart_ExistingItem() {
        CartItem existingItem = new CartItem();
        existingItem.setQuantity(2);

        when(cartItemRepository.findByUserAndProduct(user, product)).thenReturn(Optional.of(existingItem));

        cartService.addProductToCart(user, product);

        assertEquals(3, existingItem.getQuantity());
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    @Test
    void testCalculateTotal() {
        CartItem item1 = new CartItem();
        item1.setProduct(product);
        item1.setQuantity(2);

        Product product2 = new Product();
        product2.setPrice(500.0);
        CartItem item2 = new CartItem();
        item2.setProduct(product2);
        item2.setQuantity(1);

        List<CartItem> cartItems = List.of(item1, item2);

        Double total = cartService.calculateTotal(cartItems);

        assertEquals(3500.0, total);
    }
}