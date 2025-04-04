package com.example.lab6.config;

import com.example.lab6.model.Location;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocationCacheHolder {

    private static final Map<String, List<Location>> INSTANCE = new HashMap<>();

    public static Map<String, List<Location>> getInstance() {
        return INSTANCE;
    }
}