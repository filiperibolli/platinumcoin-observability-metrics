package com.platinumcoin.observability.application.usecase;


import com.platinumcoin.observability.application.domain.exception.DomainException;
import com.platinumcoin.observability.application.gateway.OrderGateway;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.platinumcoin.observability.application.domain.Order;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindOrderUseCase {
    private final OrderGateway gateway;

    @Timed(value = "orders.find.time", description = "Tempo para buscar pedido (cache ou BD)")
    public Order execute(UUID id) {
        return gateway.findById(id)
                .orElseThrow(() -> new DomainException("Order not found", "not_found", null));
    }
}