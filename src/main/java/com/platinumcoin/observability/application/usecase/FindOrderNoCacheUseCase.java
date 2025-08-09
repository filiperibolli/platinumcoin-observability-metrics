package com.platinumcoin.observability.application.usecase;

import com.platinumcoin.observability.application.domain.exception.DomainException;
import com.platinumcoin.observability.infrastructure.mapper.OrderMapper;
import com.platinumcoin.observability.infrastructure.persistence.OrderRepository;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.platinumcoin.observability.application.domain.Order;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FindOrderNoCacheUseCase {
    private final OrderRepository repository;

    @Timed(value = "orders.find.nocache.time", description = "Tempo para buscar pedido diretamente no BD")
    public Order execute(UUID id) {
        return repository.findById(id)
                .map(OrderMapper::toDomain)
                .orElseThrow(() -> new DomainException("Order not found", "not_found", null));
    }
}