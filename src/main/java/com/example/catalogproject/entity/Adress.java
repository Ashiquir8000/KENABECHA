package com.example.catalogproject.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Adress {
    private String streetAddress;
    private String city;
    private String houseNo;
    private String postCode;
}