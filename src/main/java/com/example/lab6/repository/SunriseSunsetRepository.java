package com.example.lab6.repository;

import com.example.lab6.model.SunriseSunset;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SunriseSunsetRepository extends JpaRepository<SunriseSunset, Long> {

    @EntityGraph(attributePaths = {"locations"})
    List<SunriseSunset> findAll();

    @EntityGraph(attributePaths = {"locations"})
    Optional<SunriseSunset> findById(Long id);

    @EntityGraph(attributePaths = {"locations"})
    @Query("SELECT ss FROM SunriseSunset ss WHERE ss.date = :date")
    List<SunriseSunset> findByDate(@Param("date") String date);
}