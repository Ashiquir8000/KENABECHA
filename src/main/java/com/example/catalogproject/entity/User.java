package com.example.catalogproject.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String userName;
    private String userPassword;
    private String userEmail;

    
    private String userPhoneNumber;

    @ElementCollection
    private List<String> mobileNumbers;

    @Embedded
    private Adress address;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    
    private String role = "ROLE_USER";
}