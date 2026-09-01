package com.example.catalogproject.service;

import com.example.catalogproject.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final CatalogueService catalogueService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = catalogueService.getUserByUserName(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found!");
        }

        String role = "ROLE_USER";

        if (username.equals("admin")) {
            role = "ROLE_ADMIN";
        } else if (username.equals("moderator")) {
            role = "ROLE_MODERATOR";
        } else if (username.equals("inventory")) {
            role = "ROLE_INVENTORY_MANAGER";
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getUserPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}