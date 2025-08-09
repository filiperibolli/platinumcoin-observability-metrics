package com.platinumcoin.observability.infrastructure.web.filter;

import com.platinumcoin.observability.infrastructure.persistence.entity.OrderCache;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class IdempotencyFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, OrderCache> redisTemplate;
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String key = req.getHeader("X-Idempotency-Key");
        if (key != null && Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            res.setStatus(HttpStatus.CONFLICT.value());
            res.getWriter().write("Duplicate request");
            return;
        }
        if (key != null) {
            redisTemplate.opsForValue().set(key, null, Duration.ofHours(24));
        }
        chain.doFilter(req, res);
    }
}