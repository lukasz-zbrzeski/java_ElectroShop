package com.example.shopapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@ToString(exclude = "user")
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fullName;
    private String phone;
    private String street;
    private String city;
    private String postalCode;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}

