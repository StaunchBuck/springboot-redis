package com.example.service;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private Map<Integer, String> database = new HashMap<>();

    public UserService() {
        database.put(1, "Alice");
        database.put(2, "Bob");
        database.put(3, "Charlie");
    }

    // Cacheable: First call hits DB, subsequent calls hit cache
    @Cacheable(value = "users", key = "#id")  //Cache-Aside - Lazy load into cache on first read
    public String getUser(int id) {
        System.out.println("Fetching from DB...");
        return database.get(id);
    }

    // CachePut: Updates both DB and cache
    @CachePut(value = "users", key = "#id")  //Write-Through - Updates DB and cache simultaneously
    public String updateUser(int id, String name) {
        database.put(id, name);
        return name;
    }

    // CacheEvict: Removes entry from cache
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(int id) {
        database.remove(id);
    }
}