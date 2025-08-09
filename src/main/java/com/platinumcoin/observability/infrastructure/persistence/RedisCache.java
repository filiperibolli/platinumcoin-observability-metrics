package com.platinumcoin.observability.infrastructure.persistence;

import com.platinumcoin.observability.infrastructure.persistence.entity.OrderCache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisCache {

    private final RedisTemplate<String, OrderCache> redisTemplate;

    public void put(String key, OrderCache order) {
        redisTemplate.opsForValue().set(key, order, Duration.ofMinutes(1));
    }

    public Optional<OrderCache> get(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }
}