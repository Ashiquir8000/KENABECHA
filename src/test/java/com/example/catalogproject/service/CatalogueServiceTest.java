package com.example.catalogproject.service;

import com.example.catalogproject.entity.Product;
import com.example.catalogproject.entity.User;
import com.example.catalogproject.repository.CategoryTypeRepository;
import com.example.catalogproject.repository.ProductRepository;
import com.example.catalogproject.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class CatalogueServiceTest {

    @MockitoBean
    private ProductRepository productRepository;

    @MockitoBean
    private CategoryTypeRepository categoryTypeRepository;

    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private CatalogueService catalogueService;

    @Test
    void testSaveUserInfo_FormatAndSetDefaultRole() {
        User rawUser = new User();
        rawUser.setUserName("  admin_user  "); // স্পেসসহ নাম
        rawUser.setUserPassword(" pass123 ");
        // রোল ইচ্ছাকৃতভাবে null রাখা হলো

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = catalogueService.saveUserInfo(rawUser);

        // Assertion: স্পেস ট্রিম হয়েছে এবং ডিফল্ট রোল বসেছে কি না
        assertEquals("admin_user", savedUser.getUserName());
        assertEquals("pass123", savedUser.getUserPassword());
        assertEquals("ROLE_USER", savedUser.getRole());
    }

    @Test
    void testFindByCategory() {
        Product p1 = new Product();
        p1.setCategory("Dress Collection");

        when(categoryTypeRepository.findByCategory("Dress Collection")).thenReturn(List.of(p1));

        List<Product> result = catalogueService.findByCategory("Dress Collection");

        assertEquals(1, result.size());
        assertEquals("Dress Collection", result.get(0).getCategory());
        verify(categoryTypeRepository, times(1)).findByCategory("Dress Collection");
    }
}