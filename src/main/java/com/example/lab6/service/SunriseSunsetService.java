package com.example.lab6.service;

import com.example.lab6.model.Location;
import com.example.lab6.model.SunriseSunset;
import com.example.lab6.repository.LocationRepository;
import com.example.lab6.repository.SunriseSunsetRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SunriseSunsetService {

    private static final Logger logger = LoggerFactory.getLogger(SunriseSunsetService.class);
    private static final String API_URL = "https://api.sunrise-sunset.org/json";
    private final SunriseSunsetRepository repository;
    private final LocationRepository locationRepository;
    private final RestTemplate restTemplate;
    private final Map<String, List<SunriseSunset>> sunriseSunsetCache;

    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> getSunriseSunset(double latitude, double longitude, String date, List<Long> locationIds) {
        RequestCounter.increment(); // Увеличиваем счётчик
        String url = String.format("%s?lat=%f&lng=%f&date=%s",
                API_URL, latitude, longitude, date != null ? date : "today");
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !"OK".equals(response.get("status"))) {
            throw new RuntimeException("Failed to fetch sunrise/sunset data");
        }

        SunriseSunset sunriseSunset = new SunriseSunset();
        sunriseSunset.setLatitude(latitude);
        sunriseSunset.setLongitude(longitude);
        sunriseSunset.setDate(date != null ? date : "today");

        Map<String, Object> results = (Map<String, Object>) response.get("results");
        sunriseSunset.setSunrise((String) results.get("sunrise"));
        sunriseSunset.setSunset((String) results.get("sunset"));

        if (locationIds != null && !locationIds.isEmpty()) {
            List<Location> locations = locationRepository.findAllById(locationIds);
            sunriseSunset.getLocations().addAll(locations);
        }

        repository.save(sunriseSunset);
        sunriseSunsetCache.clear();
        logger.debug("Cache cleared after fetching sunrise/sunset data");
        return response;
    }

    @Transactional(readOnly = true)
    public List<SunriseSunset> getAll() {
        RequestCounter.increment(); // Увеличиваем счётчик
        String cacheKey = "all_sunrise_sunset";
        if (sunriseSunsetCache.containsKey(cacheKey)) {
            logger.debug("Returning cached sunrise/sunset records for key: {}", cacheKey);
            return sunriseSunsetCache.get(cacheKey);
        }
        logger.debug("Cache miss, querying database for all sunrise/sunset records");
        List<SunriseSunset> sunriseSunsets = repository.findAll();
        sunriseSunsetCache.put(cacheKey, sunriseSunsets);
        return sunriseSunsets;
    }

    @Transactional(readOnly = true)
    public Optional<SunriseSunset> getById(Long id) {
        RequestCounter.increment(); // Увеличиваем счётчик
        String cacheKey = "sunrise_sunset_" + id;
        if (sunriseSunsetCache.containsKey(cacheKey)) {
            logger.debug("Returning cached sunrise/sunset record for key: {}", cacheKey);
            return Optional.ofNullable(sunriseSunsetCache.get(cacheKey).get(0));
        }
        logger.debug("Cache miss, querying database for sunrise/sunset ID: {}", id);
        Optional<SunriseSunset> sunriseSunset = repository.findById(id);
        sunriseSunset.ifPresent(ss -> sunriseSunsetCache.put(cacheKey, List.of(ss)));
        return sunriseSunset;
    }

    @Transactional(readOnly = true)
    public List<SunriseSunset> getByDate(String date) {
        RequestCounter.increment(); // Увеличиваем счётчик
        try {
            logger.info("Fetching sunrise/sunset records for date: {}", date);
            String cacheKey = "sunrise_sunset_date_" + date;
            if (sunriseSunsetCache == null) {
                logger.error("SunriseSunsetCache is null");
                throw new IllegalStateException("SunriseSunsetCache is not initialized");
            }
            logger.debug("Checking cache for key: {}", cacheKey);
            if (sunriseSunsetCache.containsKey(cacheKey)) {
                logger.info("Returning cached sunrise/sunset records for date: {}", date);
                return sunriseSunsetCache.get(cacheKey);
            }
            logger.info("Cache miss, querying database for date: {}", date);
            List<SunriseSunset> sunriseSunsets = repository.findByDate(date);
            logger.info("Found {} sunrise/sunset records for date: {}", sunriseSunsets.size(), date);
            sunriseSunsetCache.put(cacheKey, sunriseSunsets);
            return sunriseSunsets;
        } catch (Exception e) {
            logger.error("Error while fetching sunrise/sunset records for date: {}", date, e);
            throw new RuntimeException("Failed to fetch sunrise/sunset records for date: " + date, e);
        }
    }

    @Transactional
    public SunriseSunset create(SunriseSunset sunriseSunset, List<Long> locationIds) {
        RequestCounter.increment(); // Увеличиваем счётчик
        if (locationIds != null && !locationIds.isEmpty()) {
            List<Location> locations = locationRepository.findAllById(locationIds);
            sunriseSunset.getLocations().addAll(locations);
        }
        SunriseSunset saved = repository.save(sunriseSunset);
        sunriseSunsetCache.clear();
        logger.debug("Cache cleared after creating sunrise/sunset record");
        return saved;
    }

    @Transactional
    public Optional<SunriseSunset> update(Long id, SunriseSunset updatedData, List<Long> locationIds) {
        RequestCounter.increment(); // Увеличиваем счётчик
        return repository.findById(id).map(sunriseSunset -> {
            sunriseSunset.setLatitude(updatedData.getLatitude());
            sunriseSunset.setLongitude(updatedData.getLongitude());
            sunriseSunset.setDate(updatedData.getDate());
            sunriseSunset.setSunrise(updatedData.getSunrise());
            sunriseSunset.setSunset(updatedData.getSunset());

            if (locationIds != null) {
                sunriseSunset.getLocations().clear();
                List<Location> locations = locationRepository.findAllById(locationIds);
                sunriseSunset.getLocations().addAll(locations);
            }
            SunriseSunset saved = repository.save(sunriseSunset);
            sunriseSunsetCache.clear();
            logger.debug("Cache cleared after updating sunrise/sunset record ID: {}", id);
            return saved;
        });
    }

    @Transactional
    public boolean delete(Long id) {
        RequestCounter.increment(); // Увеличиваем счётчик
        return repository.findById(id).map(sunriseSunset -> {
            repository.delete(sunriseSunset);
            sunriseSunsetCache.clear();
            logger.debug("Cache cleared after deleting sunrise/sunset record ID: {}", id);
            return true;
        }).orElse(false);
    }
}