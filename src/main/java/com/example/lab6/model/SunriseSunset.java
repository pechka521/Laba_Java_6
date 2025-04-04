package com.example.lab6.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sunrise_sunset")
@Data
public class SunriseSunset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Latitude is mandatory")
    @Column(nullable = false)
    private double latitude;

    @NotNull(message = "Longitude is mandatory")
    @Column(nullable = false)
    private double longitude;

    @NotBlank(message = "Date is mandatory")
    @Column(nullable = false)
    private String date;

    @NotBlank(message = "Sunrise time is mandatory")
    @Column(nullable = false)
    private String sunrise;

    @NotBlank(message = "Sunset time is mandatory")
    @Column(nullable = false)
    private String sunset;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "sunrise_sunset_location",
            joinColumns = @JoinColumn(name = "sunrise_sunset_id"),
            inverseJoinColumns = @JoinColumn(name = "location_id")
    )
    private Set<Location> locations = new HashSet<>();
}