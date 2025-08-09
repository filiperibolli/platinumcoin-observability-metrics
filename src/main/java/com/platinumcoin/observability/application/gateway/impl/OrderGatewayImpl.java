package com.platinumcoin.observability.application.gateway.impl;


import com.platinumcoin.observability.application.domain.Order;
import com.platinumcoin.observability.application.gateway.OrderGateway;
import com.platinumcoin.observability.infrastructure.mapper.OrderMapper;
import com.platinumcoin.observability.infrastructure.persistence.OrderRepository;
import com.platinumcoin.observability.infrastructure.persistence.RedisCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderGatewayImpl implements OrderGateway {

    private final OrderRepository repository;
    private final RedisCache cache;

    @Override
    @Transactional
    public Order save(Order order) {
        var savedEntity = repository.save(OrderMapper.toEntity(order));
        var savedDomain = OrderMapper.toDomain(savedEntity);

        cache.put(savedDomain.getId().toString(), OrderMapper.toCache(savedDomain));

        log.info("[SAVE] Pedido salvo no banco e armazenado no cache. ID: {}", savedDomain.getId());

        return savedDomain;
    }

    @Override
    public Optional<Order> findById(UUID id) {
        String key = id.toString();

        return cache.get(key)
                .map(OrderMapper::toDomain)
                .map(order -> {
                    log.info("[CACHE HIT] Pedido encontrado no cache. ID: {}", id);
                    return order;
                })
                .or(() -> {
                    log.info("[CACHE MISS] Pedido não encontrado no cache. Consultando banco... ID: {}", id);

                    return repository.findById(id)
                            .map(OrderMapper::toDomain)
                            .map(order -> {
                                log.info("[DB ACCESS] Pedido encontrado no banco. ID: {}. Gravando no cache.", id);
                                cache.put(key, OrderMapper.toCache(order));
                                return order;
                            });
                });
    }
}