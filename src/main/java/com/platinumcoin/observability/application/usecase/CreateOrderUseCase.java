package com.platinumcoin.observability.application.usecase;


import com.platinumcoin.observability.application.domain.Order;
import com.platinumcoin.observability.application.gateway.OrderGateway;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreateOrderUseCase {
    private final OrderGateway gateway;
    private final Counter ordersCreated;

    public Order execute(Order order) {
        if (order.getId() == null) {
            order.setId(UUID.randomUUID());
        }
        Order saved = gateway.save(order);
        ordersCreated.increment();
        return saved;
    }
}