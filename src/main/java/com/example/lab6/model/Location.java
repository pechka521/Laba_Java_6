package com.example.lab6.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "location")
@Data
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Country is mandatory")
    @Column(nullable = false)
    private String country;

    @ManyToMany(mappedBy = "locations", fetch = FetchType.LAZY)
    private Set<SunriseSunset> sunriseSunsets = new HashSet<>();
}