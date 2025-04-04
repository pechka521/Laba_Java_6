package com.example.lab6.repository;

import com.example.lab6.model.Location;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    @EntityGraph(attributePaths = {"sunriseSunsets"})
    List<Location> findAll();

    @EntityGraph(attributePaths = {"sunriseSunsets"})
    Optional<Location> findById(Long id);

    @Query("SELECT l FROM Location l JOIN l.sunriseSunsets ss " +
            "WHERE ss.date = :date " +
            "ORDER BY l.name")
    List<Location> findLocationsBySunriseSunsetDate(@Param("date") String date);
}