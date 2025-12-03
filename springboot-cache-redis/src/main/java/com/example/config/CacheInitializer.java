package com.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.cache.CacheManager;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class CacheInitializer {

    @Value("${spring.cache.cache-names}")
    private String cacheValues;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;

    public CacheInitializer(CacheManager cacheManager,RedisTemplate<String, Object> redisTemplate) {
        this.cacheManager = cacheManager;
        this.redisTemplate = redisTemplate;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void clearCacheOnStartup() {

        // clearing spring managed cache
        cacheManager.getCacheNames().forEach(name -> {
            System.out.println("Clearing cache: " + name);
            Objects.requireNonNull(cacheManager.getCache(name)).clear();
        });

        // clearing redis aggressively
        assert redisTemplate.getConnectionFactory() != null;
//        redisTemplate.getConnectionFactory().getConnection().flushAll();  // this clears all values entriies from redis

        //selective delete
        Stream.of(cacheValues.split(",")).forEach( key -> {
            Boolean result = redisTemplate.delete(key);
            if (result) {
                System.out.println("Key '" + key + "' deleted from Redis.");
            } else {
                System.out.println("Key '" + key + "' not found in Redis.");
            }
        });
        System.out.println("All caches cleared on application startup!");

    }
}