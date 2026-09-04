package com.example.catalogproject.config;

import com.example.catalogproject.service.CatalogueService;
import com.example.catalogproject.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
                .csrf(csrf -> csrf.ignoringRequestMatchers("/ws/**"))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/", "/home", "/register", "/login", "/proDetail/**", "/category/**", "/search/visual", "/images/**", "/css/**", "/js/**", "/mobile-fix.css").permitAll()


                        .requestMatchers("/adminF", "/prodF", "/edit/**", "/delete/**", "/admin/archived", "/restore/**", "/permanent-delete/**").hasRole("ADMIN")

                        .requestMatchers("/moderator/**").hasAnyRole("ADMIN", "MODERATOR")
                        .requestMatchers("/inventory/**").hasAnyRole("ADMIN", "MODERATOR", "INVENTORY_MANAGER")
                        .requestMatchers("/cart/**", "/buy/**", "/checkout", "/addReview", "/place-order", "/ws/**").authenticated()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler((request, response, authentication) -> {
                            String username = authentication.getName();
                            User user = catalogueService.getUserByUserName(username);
                            HttpSession session = request.getSession();
                            session.setAttribute("loggedInUser", user);
                            SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
                            if (savedRequest != null) {
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