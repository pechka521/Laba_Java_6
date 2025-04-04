package com.example.lab6.controller;

import com.example.lab6.model.SunriseSunset;
import com.example.lab6.service.SunriseSunsetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sunrise-sunset")
public class SunriseSunsetController {

    private static final Logger logger = LoggerFactory.getLogger(SunriseSunsetController.class);
    private final SunriseSunsetService sunriseSunsetService;

    @Value("${sunrise-sunset.latitude:54.3000}")
    private double defaultLatitude;

    @Value("${sunrise-sunset.longitude:30.2400}")
    private double defaultLongitude;

    @GetMapping
    public ResponseEntity<List<SunriseSunset>> getAll() {
        logger.info("Getting all sunrise/sunset records");
        return ResponseEntity.ok(sunriseSunsetService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SunriseSunset> getById(@PathVariable Long id) {
        logger.info("Getting sunrise/sunset by ID: {}", id);
        return sunriseSunsetService.getById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-date")
    public ResponseEntity<?> getByDate(@RequestParam(required = false) String date) {
        if (date == null || date.trim().isEmpty()) {
            logger.warn("Date parameter is missing or empty");
            return ResponseEntity.badRequest().body("Date parameter is required");
        }
        try {
            logger.info("Getting sunrise/sunset records by date: {}", date);
            List<SunriseSunset> sunriseSunsets = sunriseSunsetService.getByDate(date);
            return ResponseEntity.ok(sunriseSunsets);
        } catch (Exception e) {
            logger.error("Error while fetching sunrise/sunset records by date: {}", date, e);
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }

    @GetMapping("/fetch")
    public ResponseEntity<Map<String, Object>> fetchSunriseSunset(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) List<Long> locationIds) {
        double lat = latitude != null ? latitude : defaultLatitude;
        double lon = longitude != null ? longitude : defaultLongitude;
        logger.info("Fetching sunrise/sunset - lat: {}, lon: {}, date: {}, locations: {}", lat, lon, date, locationIds);
        Map<String, Object> response = sunriseSunsetService.getSunriseSunset(lat, lon, date, locationIds);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<SunriseSunset> create(
            @Valid @RequestBody SunriseSunset sunriseSunset,
            @RequestParam(required = false) List<Long> locationIds) {
        logger.info("Creating sunrise/sunset record: {}", sunriseSunset);
        SunriseSunset created = sunriseSunsetService.create(sunriseSunset, locationIds);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SunriseSunset> update(
            @PathVariable Long id,
            @Valid @RequestBody SunriseSunset sunriseSunset,
            @RequestParam(required = false) List<Long> locationIds) {
        logger.info("Updating sunrise/sunset record ID: {}", id);
        return sunriseSunsetService.update(id, sunriseSunset, locationIds)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.info("Deleting sunrise/sunset record ID: {}", id);
        boolean deleted = sunriseSunsetService.delete(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}