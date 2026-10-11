package com.example.catalogproject.config;

import com.example.catalogproject.service.CatalogueService;
import com.example.catalogproject.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CatalogueService catalogueService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // CSRF ডিজেবল করা হলো যাতে লগইন, রেজিস্ট্রেশন বা অন্য কোনো POST রিকোয়েস্ট ব্লক না হয়
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/home", "/register", "/login", "/error", "/proDetail/**", "/category/**", "/search/**", "/search/visual", "/images/**", "/css/**", "/js/**", "/mobile-fix.css", "/static/**", "/favicon.ico").permitAll()
                        .requestMatchers("/adminF", "/prodF", "/edit/**", "/delete/**", "/admin/archived", "/restore/**", "/permanent-delete/**").hasRole("ADMIN")
                        .requestMatchers("/moderator/**").hasAnyRole("ADMIN", "MODERATOR")
                        .requestMatchers("/inventory", "/inventory/**").hasAnyRole("ADMIN", "MODERATOR", "INVENTORY_MANAGER")
                        .requestMatchers("/profile", "/profile/**", "/profile/update", "/cart/**", "/buy/**", "/checkout", "/addReview", "/place-order", "/ws/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .failureUrl("/login?error=true")
                        .successHandler((request, response, authentication) -> {
                            String username = authentication.getName();
                            User user = catalogueService.getUserByUserName(username);
                            HttpSession session = request.getSession();
                            if (user != null) {
                                session.setAttribute("loggedInUser", user);
                            }
                            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                            if (savedRequest != null && savedRequest.getRedirectUrl() != null && !savedRequest.getRedirectUrl().contains("/login")) {
                                response.sendRedirect(savedRequest.getRedirectUrl());
                            } else {
                                response.sendRedirect("/home");
                            }
                        })
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/home")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }

    @SuppressWarnings("deprecation")
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
}
