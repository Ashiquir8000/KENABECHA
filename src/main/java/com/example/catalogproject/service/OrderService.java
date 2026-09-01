package com.example.catalogproject.service;

import com.example.catalogproject.entity.*;
import com.example.catalogproject.repository.CustomerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final CustomerOrderRepository customerOrderRepository;
    private final InventoryService inventoryService;
    private final CartService cartService;

    @Transactional
    public CustomerOrder placeOrder(User user, String address, List<CartItem> cartItems, Double totalAmount) {
        CustomerOrder newOrder = new CustomerOrder();
        newOrder.setUser(user);
        newOrder.setOrderDate(LocalDateTime.now());
        newOrder.setStatus("PENDING");
        newOrder.setTotalAmount(totalAmount);
        newOrder.setShippingAddress(address);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setCustomerOrder(newOrder);
            orderItems.add(orderItem);

            inventoryService.reduceStock(cartItem.getProduct(), cartItem.getQuantity());
        }
        newOrder.setOrderItems(orderItems);
        CustomerOrder savedOrder = customerOrderRepository.save(newOrder);

        cartService.clearCart(user);
        return savedOrder;
    }


}